package com.teamseven.MusicVillain.Security.OAuth;

import com.teamseven.MusicVillain.Dto.ServiceResult;
import com.teamseven.MusicVillain.Utils.ENV;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import com.google.gson.JsonParser;
import com.google.gson.JsonElement;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
@Slf4j
@Service
public class OAuthService {
    /**
     *
     * @Test - needed
     */

    /* Kakao REST API reference: https://developers.kakao.com/docs/latest/ko/kakaologin/rest-api*/
    public void getKakaoAccessTokenBykakaoAuthorizationCode(String kakaoAuthorizationCode) {
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


        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public ServiceResult getKakaoMemberIdByKakaoAccessToken(String kakaoAccessToken) {

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

}
