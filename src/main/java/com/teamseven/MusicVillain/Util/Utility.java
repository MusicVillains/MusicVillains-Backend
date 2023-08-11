package com.teamseven.MusicVillain.Util;

import org.springframework.stereotype.Service;
import org.springframework.util.StreamUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class Utility {

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
