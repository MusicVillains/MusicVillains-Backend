package com.teamseven.MusicVillain.Security.OAuth;

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
            sb.append("&redirect_uri=http://localhost:3000/kakaoredirect"); // [!] Check Needed, 인가코드 받은 redirect_uri
            sb.append("&code=" + kakaoAuthorizationCode);
            bw.write(sb.toString());
            bw.flush();
            // response code == 200 ? success : fail
            int responseCode = conn.getResponseCode();
            System.out.println("responseCode : " + responseCode);

            if(responseCode != 200){
                log.trace("Failed to get Kakao member id");
                return ServiceResult.fail("Failed to get Kakao Access Token");
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

            log.trace("access_token : {}", kakaoAccessToken);
            log.trace("refresh_token : {}", kakaoRefreshToken);

            br.close();
            bw.close();
            Map<String, String> kakaoTokens = new HashMap<>();

            // for response
            kakaoTokens.put("kakaoAccessToken", kakaoAccessToken);
            kakaoTokens.put("kakaoRefreshToken", kakaoRefreshToken);

            return ServiceResult.success(kakaoTokens);

        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public ServiceResult getKakaoMemberIdByKakaoAccessToken(String kakaoAccessToken) {

        /* API reference: https://developers.kakao.com/docs/latest/ko/kakaologin/rest-api#req-user-info */
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

            int id = element.getAsJsonObject().get("id").getAsInt(); // get Kakao memberId, which is unique
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

    public ServiceResult registerMemberByKakaoMemberId(int kakaoMemberId){
        //  String generatedUserId = providerType +"_"+ oAuth2UserInfo.getId();

        // generate userId (format like KAKAO_2963007031)
        String generatedUserId = OAuth2ProviderType.KAKAO + "_" + Integer.toString(kakaoMemberId);

        // check if member(of this userId) already exists
        if(memberRepository.findByUserId(generatedUserId) != null){
            return ServiceResult.fail("Member already exists");
        }

        // create Member
        String generatedMemberId = RandomUUIDGenerator.generate();
        String generatedName = RandomNicknameGenerator.generate();

        // check if generatedName is already used
        while(memberRepository.findByName(generatedName) != null){
            generatedName = RandomNicknameGenerator.generate();
        }

        Member member = Member.builder()
                // 사용자 이메일 정보만 가지고 가입
                .memberId(UUID.randomUUID().toString().replace("-", ""))
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

        return ServiceResult.success(generatedMemberId);
    }

    public ServiceResult kakaoOauthLogin(String kakaoAuthorizationCode){

        // get kakaoAccessToken using kakaoAuthorizationCode
        ServiceResult accessTokenServiceResult
                = this.getKakaoMemberIdByKakaoAccessToken(kakaoAuthorizationCode);

        // check if kakaoMemberIdServiceResult is failed
        if (accessTokenServiceResult.isFailed()){
            return ServiceResult.fail(accessTokenServiceResult.getMessage());
        }

        // assign kakaoAccessToken(:String) from service result(result.data : Object -> Map<String, String>)
        String kakaoAccessToken =
                ((Map)accessTokenServiceResult.getData()).get("kakaoAccessToken").toString();

        log.trace("kakaoAccessToken: {}", kakaoAccessToken);

        // get kakaoMemberId using kakaoAccessToken
        ServiceResult kakaoMemberIdServiceResult
                = this.getKakaoMemberIdByKakaoAccessToken(kakaoAccessToken);

        // check if kakaoMemberIdServiceResult is failed
        if (kakaoMemberIdServiceResult.isFailed()){
            return ServiceResult.fail(kakaoMemberIdServiceResult.getMessage());
        }

        // assign kakaoMemberId from service result
        int kakaoMemberId = (int)kakaoMemberIdServiceResult.getData(); // (result.data : Object -> int)

        // get Our Member info using kakaoMemberId
        Member tmpMember = memberRepository.findByUserId
                (OAuth2ProviderType.KAKAO + "_" + Integer.toString(kakaoMemberId));

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

            // reassign tmpMember with registered Member
            tmpMember = memberRepository.findByMemberId(registeredMemberId);

        }

        // if exists or after register, return memberId and JWT Token
        String memberId = tmpMember.getMemberId();
        String memberUserId = tmpMember.getUserId();
        String memberRole = tmpMember.getRole();

        // Generate JWT Access Token
        String jwtAccessToken = JwtManager.generateToken(memberId, memberUserId, memberRole);
        String jwtRefreshToken = ""; // [!] Will be implemented later

        // return logged-in member's memberId and JWT Token
        Map<String, String> serviceResultData = new HashMap<>();
        serviceResultData.put("memberId", memberId);
        serviceResultData.put("jwtAccessToken", jwtAccessToken);
        serviceResultData.put("jwtRefreshToken", jwtRefreshToken);

        return ServiceResult.success(serviceResultData);
    }

}
