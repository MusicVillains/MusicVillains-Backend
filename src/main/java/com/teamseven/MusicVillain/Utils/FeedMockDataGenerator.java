package com.teamseven.MusicVillain.Utils;

import com.teamseven.MusicVillain.Feed.FeedService;
import io.swagger.v3.oas.annotations.Hidden;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Component
@AllArgsConstructor
public class FeedMockDataGenerator {

    private FeedService feedService;
    @Autowired
    public FeedMockDataGenerator(FeedService feedService){
        this.feedService = feedService;
    }
    List<String> musicType = Arrays.asList(
            "고음괴물","화음귀신","하이라이트도둑","힙합전사","소몰이대장","삑사리요정","기타");

    List<MockMusicObject> mockMusicObject = Arrays.asList(
            new MockMusicObject("봄날", "방탄소년단"),
            new MockMusicObject("Dynamite", "방탄소년단"),
            new MockMusicObject("Butter", "방탄소년단"),
            new MockMusicObject("Permission to Dance", "방탄소년단"),
            new MockMusicObject("사막에서 꽃을 피우듯", "우디(Woody)"),
            new MockMusicObject("신호등", "이무진"),
            new MockMusicObject("Next Level", "aespa"),
            new MockMusicObject("Weekend", "태연 (TAEYEON)"),
            new MockMusicObject("Peaches (Feat. Daniel Caesar & Giveon)", "Justin Bieber"),
            new MockMusicObject("비와 당신", "이무진"),
            new MockMusicObject("OHAYO MY NIGHT", "디핵 (D-Hack) & PATEKO"),
            new MockMusicObject("Alcohol-Free", "TWICE (트와이스)"),
            new MockMusicObject("Hype boy", "NewJeans"),
            new MockMusicObject("Kitsch", "헤이즈 (Heize)"),
            new MockMusicObject("비가 오는 날엔 (2021)", "IVE (아이브)"),
            new MockMusicObject("Still With You", "정국"),
            new MockMusicObject("우리들의 블루스", "임영웅"),
            new MockMusicObject("UNFORGIVEN (feat. Nile Rodgers)", "LE SSERAFIM (르세라핌)"),
            new MockMusicObject("Love Lee", "AKMU (악뮤)"),
            new MockMusicObject("OMG", "NewJeans"));
    List<String> memberIdList = Arrays.asList(
            "0ac1cbc9371e4808a105cce61cc57970","172ee8b7260946518984c7dc1356e436","34de4005dfe6492ca9d6c890c2305e6a",
            "5a59e53feef14302b000c92400814343", "66cbb36c99f848c0a331bcf67f296661","67012f52e22e433a86a0ff3fef8262b6");

    public void generateFeedMockData() throws IOException {
        for (int i=0; i<30; i++) {
            MockMusicObject randomMockMusicObject = mockMusicObject.get((int) (Math.random() * mockMusicObject.size()));
            String randomMemberId = memberIdList.get((int) (Math.random() * memberIdList.size()));
            String randomMusicType = musicType.get((int) (Math.random() * musicType.size()));
            feedService.insertFeed(
                    randomMemberId, randomMusicType,
                    "mock" ,0, null ,
                    randomMockMusicObject.musicName, randomMockMusicObject.musicianName);
        }
    }

}

@Hidden
@RestController
class TestController{

    private FeedMockDataGenerator feedMockDataGenerator;
    @Autowired

    public TestController(FeedMockDataGenerator feedMockDataGenerator){
        this.feedMockDataGenerator = feedMockDataGenerator;
    }

    @GetMapping("/test/generateFeedMockData")
    public String generateFeedMockData() throws IOException {
        feedMockDataGenerator.generateFeedMockData();
        return "generated";
    }


}
