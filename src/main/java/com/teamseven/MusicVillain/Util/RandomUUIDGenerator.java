package com.teamseven.MusicVillain.Util;

public class RandomUUIDGenerator {
    public static String generate(){
        return java.util.UUID.randomUUID().toString().replace("-", "");
    }
}
