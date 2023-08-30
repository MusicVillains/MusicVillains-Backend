package com.teamseven.MusicVillain.Security.OAuth;

import com.teamseven.MusicVillain.Dto.Converter.MemberDtoConverter;
import com.teamseven.MusicVillain.Dto.MemberDto;
import com.teamseven.MusicVillain.Dto.ServiceResult;
import com.teamseven.MusicVillain.Member.Member;
import com.teamseven.MusicVillain.Member.MemberRepository;
import com.teamseven.MusicVillain.Security.JWT.JwtManager;
import com.teamseven.MusicVillain.Security.MemberRole;
import com.teamseven.MusicVillain.Utils.ENV;
import com.teamseven.MusicVillain.Utils.RandomNicknameGenerator;
import com.teamseven.MusicVillain.Utils.RandomUUIDGenerator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.google.gson.JsonParser;
import com.google.gson.JsonElement;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
public class OAuthService {
    /**
     *
     * @Test - needed
     */

    private MemberRepository memberRepository;
    @Autowired
    public OAuthService(MemberRepository memberRepository){
        this.memberRepository = memberRepository;
    }

    /* Kakao REST API reference: https://developers.kakao.com/docs/latest/ko/kakaologin/rest-api */
    public ServiceResult getKakaoAccessTokenByKakaoAuthorizationCode(String kakaoAuthorizationCode) {
        log.trace("> Enter getKakaoAccessTokenByKakaoAuthorizationCode()\n"+
                "\t-with kakaoAuthorizationCode: {}",kakaoAuthorizationCode);

        // check if kakaoAuthorizationCode is null
        if(kakaoAuthorizationCode == null){
            log.error("Kakao authorization code is null");
            log.trace("> Exit getKakaoAccessTokenByKakaoAuthorizationCode()");
            return ServiceResult.fail("Kakao authorization code is null");
        }

        // send POST request to Kakao to get access token
        String kakaoAccessToken = "";
        String kakaoRefreshToken = "";
        String reqURL = "https://kauth.kakao.com/oauth/token";

        try {
            URL url = new URL(reqURL);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setDoOutput(true); // for POST

            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(conn.getOutputStream()));
            StringBuilder sb = new StringBuilder();
            sb.append("grant_type=authorization_code");
            sb.append("&client_id=" + ENV.KAKAO_CLIENT_ID); // client_id = REST API Key
            sb.append("&redirect_uri=http://localhost:3000/kakaoredirect"); // 인가코드를 받은 redirect URI
            /* sb.append("&redirect_uri=http://localhost:8080/oauth2/kakao/callback"); // when test in local */

            sb.append("&code=" + kakaoAuthorizationCode);
            bw.write(sb.toString());
            bw.flush();

            log.trace("* Send user's access token Request to Kakao(POST, {})",reqURL);

            // response code == 200 ? success : fail
            int responseCode = conn.getResponseCode();
            log.trace("* responseCode : " + responseCode);

            if(responseCode != 200){
                log.error("Failed to get Kakao access token");
                log.trace("> Exit getKakaoAccessTokenByKakaoAuthorizationCode()");

                return ServiceResult.fail("Failed to get Kakao access token");
            }

            // get response body from conn.getInputStream
            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line = "";
            String responseBody = "";

            while ((line = br.readLine()) != null) {
                responseBody += line;
            }
           log.trace("responseBody : {}", responseBody);

            // Get access_token, refresh_token from response using JsonParser
            JsonParser parser = new JsonParser();
            JsonElement element = parser.parse(responseBody);

            kakaoAccessToken = element.getAsJsonObject().get("access_token").getAsString();
            kakaoRefreshToken = element.getAsJsonObject().get("refresh_token").getAsString();

            log.trace("> Kakao access token and refresh token received\n"
                    + "\t- access_token : {}\n"
                    + "\t- refresh_token : {}", kakaoAccessToken, kakaoRefreshToken);


            br.close();
            bw.close();
            Map<String, String> kakaoTokens = new HashMap<>();

            // for response
            kakaoTokens.put("kakaoAccessToken", kakaoAccessToken);
            kakaoTokens.put("kakaoRefreshToken", kakaoRefreshToken);

            log.trace("> Exit getKakaoAccessTokenByKakaoAuthorizationCode()");
            return ServiceResult.success(kakaoTokens);

        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public ServiceResult getKakaoMemberIdByKakaoAccessToken(String kakaoAccessToken) {

        /* Kakao Rest API reference:
           - https://developers.kakao.com/docs/latest/ko/kakaologin/rest-api#req-user-info */

        String reqURL = "https://kapi.kakao.com/v2/user/me";

        //access_token을 이용하여 사용자 정보 조회
        try {
            URL url = new URL(reqURL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setRequestProperty("Authorization", "Bearer " + kakaoAccessToken); // put kakaoAccessToken to header

            // check response code (200 ? success : fail)
            int responseCode = conn.getResponseCode();
            log.trace("responseCode : {}", responseCode);
            if(responseCode != 200){
                log.trace("Failed to get Kakao member id");
                return ServiceResult.fail("Failed to get Kakao member id");
            }
            // get response body from conn.getInputStream
            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line = "";
            String responseBody = "";

            while ((line = br.readLine()) != null) {
                responseBody += line;
            }
            log.trace("responseBody : {}", responseBody);

            // Parsing JSON response body using JsonParser
            JsonParser parser = new JsonParser();
            JsonElement element = parser.parse(responseBody);

            Long id = element.getAsJsonObject().get("id").getAsLong(); // get Kakao memberId, which is unique
            /*
            boolean hasEmail = element.getAsJsonObject().get("kakao_account").getAsJsonObject().get("has_email").getAsBoolean();
            String email = "";
            if (hasEmail) {
                email = element.getAsJsonObject().get("kakao_account").getAsJsonObject().get("email").getAsString();
            }
            */
            br.close();
            return ServiceResult.success(id);

        } catch (IOException e) {
            e.printStackTrace();
            return ServiceResult.fail("Failed to get Kakao member id");
        }
    }

    public ServiceResult registerMemberByKakaoMemberId(Long kakaoMemberId){
        log.trace("> Enter registerMemberByKakaoMemberId()\n"+
                "\t- with kakaoMemberId: {}", kakaoMemberId);

        // generate userId (format like KAKAO_2963007031)

        String generatedUserId = OAuth2ProviderType.KAKAO + "_" + kakaoMemberId;
        log.trace("* expected userId...\n" +"\tgeneratedUserId: {}", generatedUserId);

        // check if member(of this userId) already exists
        if(memberRepository.findByUserId(generatedUserId) != null){
            log.error("Member already exists");
            return ServiceResult.fail("Member already exists");
        }
        log.error("* register new member...");

        // create Member
        String generatedMemberId = RandomUUIDGenerator.generate();
        String generatedName = RandomNicknameGenerator.generate();

        // check if generatedName is already used
        while(memberRepository.findByName(generatedName) != null){
            generatedName = RandomNicknameGenerator.generate();
        }

        Member member = Member.builder()
                .memberId(generatedMemberId)
                .providerType(OAuth2ProviderType.KAKAO)
                .userId(generatedUserId)
                .name(generatedName) // user nickname
                .userInfo("-")
                .email("-")
                .role(MemberRole.USER)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
        memberRepository.save(member);

        log.trace("* Member registered successfully\n" +
                "\t- memberId: {}\n" +
                "\t- userId: {}\n" +
                "\t- name: {}\n"+
                "\t- role: {}\n"+
                "\t- providerType: {}"
                , member.getMemberId(), member.getUserId(), member.getName(), member.getRole(), member.getProviderType());

        log.trace("> Exit registerMemberByKakaoMemberId()");
        return ServiceResult.success(generatedMemberId);
    }

    public ServiceResult kakaoOauthLogin(String kakaoAuthorizationCode){
        log.trace("> Enter KakaoOauthLogin()\n"
                +"\t-with kakaoAuthorizationCode: {}", kakaoAuthorizationCode);

        log.trace("kakaoOauthLogin -> get kakaoAccessToken using kakaoAuthorizationCode");
        // get kakaoAccessToken using kakaoAuthorizationCode
        ServiceResult accessTokenServiceResult
                = this.getKakaoAccessTokenByKakaoAuthorizationCode(kakaoAuthorizationCode);

        // check if kakaoMemberIdServiceResult is failed
        if (accessTokenServiceResult.isFailed()){
            return ServiceResult.fail(accessTokenServiceResult.getMessage());
        }

        // assign kakaoAccessToken(:String) from service result(result.data : Object -> Map<String, String>)
        String kakaoAccessToken =
                ((Map)accessTokenServiceResult.getData()).get("kakaoAccessToken").toString();

        log.trace("kakaoAccessToken: {}", kakaoAccessToken);


        log.trace("kakaoOauthLogin -> get kakaoMemberId using kakaoAccessToken");

        // get kakaoMemberId using kakaoAccessToken
        ServiceResult kakaoMemberIdServiceResult
                = this.getKakaoMemberIdByKakaoAccessToken(kakaoAccessToken);

        // check if kakaoMemberIdServiceResult is failed
        if (kakaoMemberIdServiceResult.isFailed()){
            return ServiceResult.fail(kakaoMemberIdServiceResult.getMessage());
        }

        // assign kakaoMemberId from service result
        Long kakaoMemberId = (Long)kakaoMemberIdServiceResult.getData(); // (result.data : Object -> int)

        // get Our Member info using kakaoMemberId
        Member tmpMember = memberRepository.findByUserId
                (OAuth2ProviderType.KAKAO + "_" + kakaoMemberId);

        String kakaoOauthLoginServiceResultMessage = "";

        // check if member(of this userId) already exists
        if(tmpMember == null){
           // if not exists, register member using kakaoMemberId
            ServiceResult registerResult = this.registerMemberByKakaoMemberId(kakaoMemberId);
            // check if register process failed
            if(registerResult.isFailed()){
                return ServiceResult.fail(registerResult.getMessage());
            }

            // if success, get registered(which is null) Member's MemberId
            String registeredMemberId =  registerResult.getData().toString();
            log.trace("registeredMemberId: {} ", registeredMemberId);
            // reassign tmpMember with registered Member
            tmpMember = memberRepository.findByMemberId(registeredMemberId);
            log.trace("check if member exists in DB -> tmpMember {}",tmpMember);
            kakaoOauthLoginServiceResultMessage = "Member registered successfully";

        }

        // if exists or after register, return memberId and JWT Token
        String memberId = tmpMember.getMemberId();
        String memberUserId = tmpMember.getUserId();
        String memberRole = tmpMember.getRole();

        // Generate JWT Access Token
        String accessToken = JwtManager.generateAccessToken(memberId, memberUserId, memberRole);
        String refreshToken = JwtManager.generateRefreshToken(memberId, memberUserId, memberRole);

        // return logged-in member's memberId and JWT Token
        Map<String, Object> serviceResultData = new HashMap<>();
        MemberDtoConverter memberDtoConverter = new MemberDtoConverter();

        MemberDto memberDto = memberDtoConverter.convertToDto(tmpMember);

        /* Not response Member info(Token contains MemberId) */
        // serviceResultData.put("member", memberDto);
        serviceResultData.put("tokenType", "Bearer");
        serviceResultData.put("accessToken", accessToken);
        serviceResultData.put("refreshToken", refreshToken);

        log.trace("* Service result of KakaoOauthLogin\n");
        log.trace("\tmember: {}\n", memberDto,
                "\taccessToken: {}\n", accessToken,
                "\trefreshToken: {}\n", refreshToken);

        if(kakaoOauthLoginServiceResultMessage == ""){
            kakaoOauthLoginServiceResultMessage = "Logged in successfully";
        }

        log.trace("> Exit KakaoOauthLogin()");
        return ServiceResult.builder()
                .result(ServiceResult.SUCCESS)
                .message(kakaoOauthLoginServiceResultMessage)
                .data(serviceResultData)
                .build();
    }

}
