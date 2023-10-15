package com.teamseven.MusicVillain;

import com.teamseven.MusicVillain.Dto.FeedDto;
import com.teamseven.MusicVillain.Dto.ResponseBody.CustomResponseBody;
import com.teamseven.MusicVillain.Feed.FeedController;
import com.teamseven.MusicVillain.Member.MemberController;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.mapping.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Nested;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestHeader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import static org.junit.Assert.fail;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@Transactional
@Slf4j
public class FeedControllerTest {
    private static HttpHeaders headers;

    @Autowired
    private FeedController feedController;

    @BeforeAll
    public static void setHeaders(){
        headers = new HttpHeaders();
    }

    @Test
    @DisplayName("특정 필드 조회하는 테스트")
    void getFeedByIdTest() {
        // given
        HttpStatusCode expected = HttpStatusCode.valueOf(200);
        // when
        HttpStatusCode actual = feedController.getFeedById("1", new HttpHeaders()).getStatusCode();
        // then
        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("비회원이 모든 피드를 조회하는 테스트")
    void getAllFeedsTest(){
        // given
        HttpStatusCode expected = HttpStatusCode.valueOf(200);
        // when
        HttpStatusCode actual = feedController.getAllFeeds(new HttpHeaders()).getStatusCode();
        // then
        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("타입별 피드 조회하는 테스트")
    void getAllFeedsByFeedTypeTest(){
        // given
        HttpStatusCode expected = HttpStatusCode.valueOf(200);
        // when
        HttpStatusCode actual = feedController.getAllFeedsByFeedType("고음괴물",new HttpHeaders()).getStatusCode();
        // then
        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("피드 녹음본 조회하는 테스트")
    void getFeedRecordTest(){
        // given
        int expected = 200;
        // when
        int actual = feedController.getFeedRecord("035f987540c94c7785b094a976f9dced").getStatusCode();
        // then
        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("멤버별 피드 조회하는 테스트")
    void getFeedByMemberIdTest(){
        // given
        HttpStatusCode expected = HttpStatusCode.valueOf(200);
        // when
        HttpStatusCode actual = feedController.getFeedByMemberId("1").getStatusCode();
        // then
        assertEquals(expected, actual);
    }

    @Autowired
    private MemberController memberController;

    @Test
    @DisplayName("피드 생성하는 테스트")
    void createFeedTest() throws IOException {
        // given
        /*TODO : Authorization 처리 후 200으로 변경*/
//        HttpStatusCode expected = HttpStatusCode.valueOf(200);
        HttpStatusCode expected = HttpStatusCode.valueOf(401);
        MultipartFile multipartFile = new MockMultipartFile("name", "originalFileName.txt", "text/plain", "File content".getBytes()); // MockMultipartFile 생성
        // when
        HttpStatusCode actual = feedController.createFeed("172ee8b7260946518984c7dc1356e436", "musicName", "musicianName", "feedType", "feedDescription", 5, multipartFile, headers).getStatusCode();
        // then
        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("피드 삭제하는 테스트")
    void deleteFeedTest(){
        // given
        /*TODO : Authorization 처리 후 200으로 변경*/
//        HttpStatusCode expected = HttpStatusCode.valueOf(200);
        HttpStatusCode expected = HttpStatusCode.valueOf(401);
        // when
        HttpStatusCode actual = feedController.deleteFeed("0097ad4148c3485f94d05b44714da623", headers).getStatusCode();
        // then
        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("피드 조회수 늘리는 테스트")
    void viewCountUpTest(){
        // given
        HttpStatusCode expected = HttpStatusCode.valueOf(200);
        // when
        HttpStatusCode actual = feedController.viewCountUp("0097ad4148c3485f94d05b44714da623").getStatusCode();
        // then
        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("좋아요를 누른 모든 피드 조회하는 테스트")
    void getInteractionFeedsByMemberIdTest(){
        // given
        /*TODO : Authorization 처리 후 200으로 변경*/
//        HttpStatusCode expected = HttpStatusCode.valueOf(200);
        HttpStatusCode expected = HttpStatusCode.valueOf(401);
        // when
        HttpStatusCode actual = feedController.getInteractionFeedsByMemberId("66cbb36c99f848c0a331bcf67f296661", headers).getStatusCode();
        // then
        assertEquals(expected, actual);
    }

}
