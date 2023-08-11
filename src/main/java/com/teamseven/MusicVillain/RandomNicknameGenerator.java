package com.teamseven.MusicVillain;

import com.teamseven.MusicVillain.Member.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

@Component
public class RandomNicknameGenerator {

    // 위에 주석한 내용 전부 하나씩 firsWords로 초기화
    public static final List<String> firstWords = Arrays.asList(
            "활동적인", "자기만족의", "독립심이 강한", "예술적인", "협동적인", "청순한", "의식이 있는", "창조적인", "경계심이 강한", "사려 깊은",
            "예속적인", "성실한", "독단적인", "관대한", "성숙한", "능력 있는", "온후한", "명랑한", "유능한", "낙천적인");

    public static final List<String> secondWords = Arrays.asList(
            "소피", "엠마", "올리비아", "이자벨라", "아바", "미아", "에밀리", "아비가일", "메디슨", "엘리자베스",
            "샬롯", "에이버리", "소피아", "클로이", "엘라", "하퍼", "아멜리아", "오브리", "에디슨", "에블린");

    public static String generate() {
        Random random = new Random();
        if (firstWords.isEmpty() || secondWords.isEmpty()) {
            return "No words available for generating nickname.";
        }

        String randomFirstWord = firstWords.get(random.nextInt(firstWords.size()));
        String randomSecondWord = secondWords.get(random.nextInt(secondWords.size()));

        int randomNumber = random.nextInt(100); // Change this range if needed

        String tempNickname = randomFirstWord + randomSecondWord + randomNumber;

        /* 생성된 닉네임이 이미 존재하는지 여부는 호출하는 method 에서 확인해야함 */

        return randomFirstWord + " " + randomSecondWord + randomNumber;
    }

}
