package com.teamseven.MusicVillain.Utils;

public class RandomUUIDGenerator {
    public static String generate(){
        return java.util.UUID.randomUUID().toString().replace("-", "");
    }
}
