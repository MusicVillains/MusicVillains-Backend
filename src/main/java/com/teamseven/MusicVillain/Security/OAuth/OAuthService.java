package com.teamseven.MusicVillain.Security.OAuth;

import com.auth0.jwt.JWT;
import com.teamseven.MusicVillain.Dto.Converter.DateConverter;
import com.teamseven.MusicVillain.Dto.Converter.MemberDtoConverter;
import com.teamseven.MusicVillain.Dto.MemberDto;
import com.teamseven.MusicVillain.Dto.ServiceResult;
import com.teamseven.MusicVillain.Member.Member;
import com.teamseven.MusicVillain.Member.MemberRepository;
import com.teamseven.MusicVillain.Security.JWT.JwtManager;
import com.teamseven.MusicVillain.Security.JWT.JwtToken;
import com.teamseven.MusicVillain.Security.JWT.JwtTokenRepository;
import com.teamseven.MusicVillain.Security.MemberRole;
import com.teamseven.MusicVillain.Utils.ENV;
import com.teamseven.MusicVillain.Utils.RandomNicknameGenerator;
import com.teamseven.MusicVillain.Utils.RandomUUIDGenerator;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

@Slf4j
@Service
@RequiredArgsConstructor
public class OAuthService {
    /**
     *
     * @Test - needed
     */

    private final MemberRepository memberRepository;
    private final JwtTokenRepository jwtTokenRepository;

    /* Kakao REST API reference: https://developers.kakao.com/docs/latest/ko/kakaologin/rest-api */
    public ServiceResult getKakaoAccessTokenByKakaoAuthorizationCode(String kakaoAuthorizationCode, String redirectUri) {
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
            sb.append("&redirect_uri=" + redirectUri); // 인가코드를 받은 redirect URI
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

    public ServiceResult getKakaoAccessTokenByKakaoAuthorizationCode_TestOnly(String kakaoAuthorizationCode,
                                                                              String backendRedirectUri) {
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
            sb.append("&redirect_uri=" + backendRedirectUri); // 인가코드를 받은 redirect URI
            /* WARN: Change after test */
            // sb.append("&redirect_uri=http://localhost:8080/oauth2/kakao/callback"); // when test in local

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

    /* WARN: test needed */
    public ServiceResult kakaoOauthLogin(String kakaoAuthorizationCode, String redirectUri){
        log.trace("> Enter KakaoOauthLogin()\n"
                +"\t-with kakaoAuthorizationCode: {}", kakaoAuthorizationCode);

        log.trace("kakaoOauthLogin -> get kakaoAccessToken using kakaoAuthorizationCode");
        // get kakaoAccessToken using kakaoAuthorizationCode
        ServiceResult accessTokenServiceResult
                = this.getKakaoAccessTokenByKakaoAuthorizationCode
                (kakaoAuthorizationCode,redirectUri);

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
        String generatedAccessToken = JwtManager.generateAccessToken(memberId, memberUserId, memberRole);
        String generatedRefreshToken = JwtManager.generateRefreshToken(memberId, memberUserId, memberRole);

        // check if there's already JWT Token of this member in DB
        JwtToken accessTokenEntity = jwtTokenRepository.findByOwnerIdAndType(memberId,"access");
        JwtToken refreshTokenEntity = jwtTokenRepository.findByOwnerIdAndType(memberId, "refresh");

        if(accessTokenEntity == null) {
            accessTokenEntity = JwtToken.builder()
                .tokenId(RandomUUIDGenerator.generate())
                .ownerId(JWT.decode(generatedAccessToken).getClaim("memberId").asString())
                .type(JWT.decode(generatedAccessToken).getClaim("type").asString())
                .value(generatedAccessToken)
                .expiredAt(DateConverter.convertDateToLocalDateTime(JWT.decode(generatedAccessToken).getExpiresAt()))
                .build();
        }else{
            // update accessTokenEntity
            accessTokenEntity.value = generatedAccessToken;
            accessTokenEntity.expiredAt =
                    DateConverter.convertDateToLocalDateTime(JWT.decode(generatedAccessToken).getExpiresAt());
        }

        if(refreshTokenEntity == null) {
            refreshTokenEntity = JwtToken.builder()
                .tokenId(RandomUUIDGenerator.generate())
                .ownerId(JWT.decode(generatedRefreshToken).getClaim("memberId").asString())
                .type(JWT.decode(generatedRefreshToken).getClaim("type").asString())
                .value(generatedRefreshToken)
                .expiredAt(DateConverter.convertDateToLocalDateTime(JWT.decode(generatedRefreshToken).getExpiresAt()))
                .build();
        }else{
            // update refreshTokenEntity
            refreshTokenEntity.value = generatedRefreshToken;
            refreshTokenEntity.expiredAt = DateConverter.convertDateToLocalDateTime(JWT.decode(generatedRefreshToken).getExpiresAt());
        }

        jwtTokenRepository.save(accessTokenEntity);
        jwtTokenRepository.save(refreshTokenEntity);

        // return logged-in member's memberId and JWT Token
        Map<String, Object> serviceResultData = new HashMap<>();
        MemberDtoConverter memberDtoConverter = new MemberDtoConverter();

        MemberDto memberDto = memberDtoConverter.convertToDto(tmpMember);

        /* Not response Member info(Token contains MemberId) */
        // serviceResultData.put("member", memberDto);

        serviceResultData.put("memberId", memberDto.getMemberId()); // just for convenienc
        serviceResultData.put("tokenType", "Bearer");
        serviceResultData.put("accessToken", generatedAccessToken);
        serviceResultData.put("refreshToken", generatedRefreshToken);

        log.trace("* Service result of KakaoOauthLogin\n");
        log.trace("\tmember: {}\n", memberDto,
                "\taccessToken: {}\n", generatedAccessToken,
                "\trefreshToken: {}\n", generatedRefreshToken);

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


    public ServiceResult kakaoOauthLogin_TestOnly(String kakaoAuthorizationCode){
        log.trace("> Enter KakaoOauthLogin()\n"
                +"\t-with kakaoAuthorizationCode: {}", kakaoAuthorizationCode);

        log.trace("kakaoOauthLogin -> get kakaoAccessToken using kakaoAuthorizationCode");
        // get kakaoAccessToken using kakaoAuthorizationCode
        ServiceResult accessTokenServiceResult
                = this.getKakaoAccessTokenByKakaoAuthorizationCode_TestOnly(kakaoAuthorizationCode
        , "http://localhost:8080/oauth2/kakao/callback");

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
        String generatedAccessToken = JwtManager.generateAccessToken(memberId, memberUserId, memberRole);
        String generatedRefreshToken = JwtManager.generateRefreshToken(memberId, memberUserId, memberRole);

        // check if there's already JWT Token of this member in DB
        JwtToken accessTokenEntity = jwtTokenRepository.findByOwnerIdAndType(memberId,"access");
        JwtToken refreshTokenEntity = jwtTokenRepository.findByOwnerIdAndType(memberId, "refresh");

        if(accessTokenEntity == null) {
            accessTokenEntity = JwtToken.builder()
                    .tokenId(RandomUUIDGenerator.generate())
                    .ownerId(JWT.decode(generatedAccessToken).getClaim("memberId").asString())
                    .type(JWT.decode(generatedAccessToken).getClaim("type").asString())
                    .value(generatedAccessToken)
                    .expiredAt(DateConverter.convertDateToLocalDateTime(JWT.decode(generatedAccessToken).getExpiresAt()))
                    .build();
        }else{
            // update accessTokenEntity
            accessTokenEntity.value = generatedAccessToken;
            accessTokenEntity.expiredAt =
                    DateConverter.convertDateToLocalDateTime(JWT.decode(generatedAccessToken).getExpiresAt());
        }

        if(refreshTokenEntity == null) {
            refreshTokenEntity = JwtToken.builder()
                    .tokenId(RandomUUIDGenerator.generate())
                    .ownerId(JWT.decode(generatedRefreshToken).getClaim("memberId").asString())
                    .type(JWT.decode(generatedRefreshToken).getClaim("type").asString())
                    .value(generatedRefreshToken)
                    .expiredAt(DateConverter.convertDateToLocalDateTime(JWT.decode(generatedRefreshToken).getExpiresAt()))
                    .build();
        }else{
            // update refreshTokenEntity
            refreshTokenEntity.value = generatedRefreshToken;
            refreshTokenEntity.expiredAt = DateConverter.convertDateToLocalDateTime(JWT.decode(generatedRefreshToken).getExpiresAt());
        }

        jwtTokenRepository.save(accessTokenEntity);
        jwtTokenRepository.save(refreshTokenEntity);

        // return logged-in member's memberId and JWT Token
        Map<String, Object> serviceResultData = new HashMap<>();
        MemberDtoConverter memberDtoConverter = new MemberDtoConverter();

        MemberDto memberDto = memberDtoConverter.convertToDto(tmpMember);

        /* Not response Member info(Token contains MemberId) */
        // serviceResultData.put("member", memberDto);

        serviceResultData.put("memberId", memberDto.getMemberId()); // just for convenienc
        serviceResultData.put("tokenType", "Bearer");
        serviceResultData.put("accessToken", generatedAccessToken);
        serviceResultData.put("refreshToken", generatedRefreshToken);

        log.trace("* Service result of KakaoOauthLogin\n");
        log.trace("\tmember: {}\n", memberDto,
                "\taccessToken: {}\n", generatedAccessToken,
                "\trefreshToken: {}\n", generatedRefreshToken);

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

    @Transactional
    /* TODO: need to clean up */
    /* WARN: 현재 try catch 구문에서 리턴이 안되서, 토큰이 만료 되었음에도 불구하고 unlink를 진행하는 문제가 있음 */
    // authorization: 로그아웃 시도하는 사용자의 Access Token
    public ServiceResult kakaoOauthLogout(String memberId
//            ,String authorization
    ){
        /* ──────────────────────────────────────────────────────────────────────── */
        /* WARN: 프론트엔드 요청으로 일시적으로 인가처리 비활성화, Token Refresh가 잘 안된다고 함 */
        /*
        String accessToken = authorization.replace("Bearer ", "");

        // find if this token exists in DB
        JwtToken tmpAccessTokenEntity = jwtTokenRepository.findByValueAndType
                (accessToken,"access");

        if(tmpAccessTokenEntity == null){
            return ServiceResult.fail("Access token is not valid");
        }

        // if exists, verify token
        ServiceResult tokenVerfiyResult = JwtManager.verifyAccessToken(authorization);
        if (tokenVerfiyResult.isFailed()) // if failed
            return ServiceResult.fail(tokenVerfiyResult.getMessage());
        */
        /* ──────────────────────────────────────────────────────────────────────── */


        /* ──────────────────────────────────────────────────────────────────────── */
        /*  WARN: 문제 확인전까지 memberId 직접 받아서 로그아웃 처리하도록 수정 */
        // check if it's exists Member
        Member tmpMember = memberRepository.findByMemberId(memberId);

        // if not exists, return fail
        if(tmpMember == null){
            return ServiceResult.fail("Member not exists");
        }
        /* ──────────────────────────────────────────────────────────────────────── */



        // if verified, do logout
        log.trace("delete access token from DB");

        /* ──────────────────────────────────────────────────────────────────────── */
        /* WARN: 프론트엔드 요청으로 일시적으로 인가처리 비활성화, Token Refresh가 잘 안된다고 함 */
        /* String memberId = JWT.decode(accessToken).getClaim("memberId").asString(); */
        /* ──────────────────────────────────────────────────────────────────────── */

        log.trace("memberId: {}", memberId);
        // delete token from DB( delete access, refresh token of this user)
        jwtTokenRepository.deleteAllByOwnerIdAndType(memberId, JwtManager.TYPE_ACCESS_TOKEN());
        jwtTokenRepository.deleteAllByOwnerIdAndType(memberId, JwtManager.TYPE_REFRESH_TOKEN());

        return ServiceResult.of(ServiceResult.SUCCESS, "Logged out successfully");
    }

    /* TODO: Implement Later */
    public ServiceResult refreshAccessToken(String refreshToken){
        // refresh token null 체크
        if(refreshToken == null) return ServiceResult.fail("Refresh token is null");
        // refresh 토큰이 유효한지 db에서 확인, findByValueAndType(refreshToken, "refresh")
        JwtToken tmpRefreshTokenEntity = jwtTokenRepository.findByValueAndType(refreshToken, "refresh");
        if(tmpRefreshTokenEntity == null) return ServiceResult.fail("Refresh token is not valid");

        // 유효하면 토큰 verfiy를 통해 만료된 토큰인지 확인
        ServiceResult verifyRefreshTokenResult = JwtManager.verifyRefreshToken(refreshToken);
        if(verifyRefreshTokenResult.isFailed())
            return ServiceResult.fail(verifyRefreshTokenResult.getMessage());

        String memberId = JWT.decode(refreshToken).getClaim("memberId").asString();
        String userId = JWT.decode(refreshToken).getClaim("userId").asString();
        String memberRole = JWT.decode(refreshToken).getClaim("role").asString();

        // 토큰 검증에 성공한 경우 새로운 access token 발급 및 데이터베이스 저장 후 리턴
        String generatedAccessToken = JwtManager.generateAccessToken(memberId, userId, memberRole);

        // check if there's already Access Token of this member in DB
        JwtToken tmpAccessTokenEntity = jwtTokenRepository.findByOwnerIdAndType(memberId,JwtManager.TYPE_ACCESS_TOKEN());

        if(tmpAccessTokenEntity == null){
            tmpAccessTokenEntity = JwtToken.builder()
                    .tokenId(RandomUUIDGenerator.generate())
                    .ownerId(JWT.decode(generatedAccessToken).getClaim("memberId").asString())
                    .type(JWT.decode(generatedAccessToken).getClaim("type").asString())
                    .value(generatedAccessToken)
                    .expiredAt(DateConverter.convertDateToLocalDateTime(JWT.decode(generatedAccessToken).getExpiresAt()))
                    .build();
        }else{
            tmpAccessTokenEntity.value = generatedAccessToken;
            tmpAccessTokenEntity.expiredAt =
                    DateConverter.convertDateToLocalDateTime(JWT.decode(generatedAccessToken).getExpiresAt());
        }
        jwtTokenRepository.save(tmpAccessTokenEntity);


        return ServiceResult.of(ServiceResult.SUCCESS, "Refreshed successfully", generatedAccessToken);
    }

    /* TODO: Implement Later */
    // 회원 탈퇴시 카카오에서도 연결을 끊어주기 위한 method
    /* Kakao Rest API Reference:
        https://developers.kakao.com/docs/latest/ko/kakaologin/rest-api#unlink */

    /* Request URI: https://kapi.kakao.com/v1/user/unlink */
    public ServiceResult unlinkMember(String authorization){
        if(authorization == null)
            return ServiceResult.fail("Authorization is null");

        String accessToken = authorization.replace("Bearer ", "");
        Boolean expired = false;
        // find if this token exists in DB
        JwtToken tmpAccessTokenEntity = jwtTokenRepository.findByValueAndType
                (accessToken,"access");

        if(tmpAccessTokenEntity == null){
            return ServiceResult.fail("Access token is not valid");
        }

        // if exists, verifiy token
        ServiceResult tokenVerfiyResult = JwtManager.verifyAccessToken(authorization);
        if (tokenVerfiyResult.isFailed()) // if failed
            return ServiceResult.fail(tokenVerfiyResult.getMessage());

        // 유효한 토큰일 경우 access token으로부터 userId를 가져옴
        String userId = JWT.decode(accessToken).getClaim("userId").asString();
        log.trace("userId: {}", userId);
        // userId에서 providerType와 provider측에서 사용하는 id(식별자)를 분리
        String providerType = userId.split("_")[0];
        Long kakaoMemberIdentifier = Long.parseLong(userId.split("_")[1]);

        // providerType 이 "KAKAO" 인지 확인
        if(!providerType.equals(OAuth2ProviderType.KAKAO)){
            return ServiceResult.fail("Not a Kakao member");
        }

        try {
            // 카카오 연결 끊기
            String reqURL = "https://kapi.kakao.com/v1/user/unlink";
            URL url = new URL(reqURL);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Authorization", "KakaoAK " + "3ed2be45d9d3ae9cfde98441da761b47"); // put kakaoAccessToken to header

            conn.setDoOutput(true); // for POST

            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(conn.getOutputStream()));
            StringBuilder sb = new StringBuilder();
            sb.append("target_id_type=user_id"); // user_id로 고정
            sb.append("&target_id=" + kakaoMemberIdentifier);
            bw.write(sb.toString());
            bw.flush();

            log.trace("* Send unlink request to Kakao(POST, {})", reqURL);

            // response code == 200 ? success : fail
            int responseCode = conn.getResponseCode();
            log.trace("* responseCode : " + responseCode);

            if (responseCode != 200) {
                log.error("Failed to Unlik Kakao member");
                return ServiceResult.fail("Failed to Unlik Kakao member");
            }
        } catch(Exception e){
            e.printStackTrace();
            return ServiceResult.fail("Failed to unlink Kakao member");
        }

        return ServiceResult.success("Unlinked successfully");
    }
}
