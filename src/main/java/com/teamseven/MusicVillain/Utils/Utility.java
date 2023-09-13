package com.teamseven.MusicVillain.Utils;

import org.springframework.web.multipart.MultipartFile;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.regex.Pattern;

public class Utility {

    public static boolean isValidUUID(String uuidStrWithoutHyphen){
        if(uuidStrWithoutHyphen == null) return false;

        // 입력 문자열이 null이거나 32자리가 아니면 false
        String uuidPattern = "^[0-9a-fA-F]{32}$";

        // 입력 문자열이 UUID 패턴과 일치하는지 확인
        return Pattern.matches(uuidPattern, uuidStrWithoutHyphen);
    }
    public byte[] AudioCutter(MultipartFile audioFile, int startTime, int endTime) throws IOException {
        try {
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(audioFile.getInputStream());
            AudioFormat format = audioInputStream.getFormat();

            long startByte = (long) (startTime * format.getFrameSize() * format.getFrameRate());
            long endByte = (long) (endTime * format.getFrameSize() * format.getFrameRate());

            AudioInputStream cutAudioStream = new AudioInputStream(audioInputStream, format, endByte - startByte);

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            AudioSystem.write(cutAudioStream, AudioFileFormat.Type.WAVE, outputStream);

            return outputStream.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
            throw new IOException("Failed to cut audio.");
        }
    }
}
