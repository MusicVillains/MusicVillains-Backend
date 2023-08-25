package com.teamseven.MusicVillain.Security.OAuth;

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
        String accessToken = "";
        String refreshToken = "";
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

            // Get Response(json fomat) from conn.getInputStream
            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line = "";
            String result = "";

            while ((line = br.readLine()) != null) {
                result += line;
            }
           log.debug("response body : " + result);

            //Get access_token, refresh_token from response using JsonParser
            JsonParser parser = new JsonParser();
            JsonElement element = parser.parse(result);

            accessToken = element.getAsJsonObject().get("access_token").getAsString();
            refreshToken = element.getAsJsonObject().get("refresh_token").getAsString();

            log.debug("access_token : " + accessToken);
            log.debug("refresh_token : " + refreshToken);

            br.close();
            bw.close();


        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
