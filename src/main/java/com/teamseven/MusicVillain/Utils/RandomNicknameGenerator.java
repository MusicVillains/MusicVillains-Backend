package com.teamseven.MusicVillain.Utils;

import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

@Component
public class RandomNicknameGenerator{

    public static final List<String> firstWords = Arrays.asList(
            "활동적인", "자기만족의", "독립심이 강한", "예술적인", "협동적인", "청순한", "의식이 있는", "창조적인", "경계심이 강한", "사려 깊은",
            "예속적인", "성실한", "독단적인", "관대한", "성숙한", "능력 있는", "온후한", "명랑한", "유능한", "낙천적인",
            "정적인", "유치한", "자존심 센", "적극적인", "감상적인", "수상쩍은", "잘난체하는","강한", "건강한", "경건한", "자존심 있는","완고한",
            "믿을만한", "로맨틱한", "빈틈없는", "포용력 있는", "명확한", "깨끗한", "안달하는", "매력이 있는", "화 잘내는", "융통성 있는");

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
