package com.teamseven.MusicVillain;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class RandomNicknameGenerator {

    public static String generate(List<String> firstWords, List<String> secondWords, Random random) {
        if (firstWords.isEmpty() || secondWords.isEmpty()) {
            return "No words available for generating nickname.";
        }

        String randomFirstWord = firstWords.get(random.nextInt(firstWords.size()));
        String randomSecondWord = secondWords.get(random.nextInt(secondWords.size()));

        int randomNumber = random.nextInt(100); // Change this range if needed

        return randomFirstWord + randomSecondWord + randomNumber;
    }

}
