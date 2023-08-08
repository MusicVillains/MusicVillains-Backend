package com.teamseven.MusicVillain;

import lombok.Builder;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class ResponseDto {
    public int statusCode;
    public String message;
    public Object data;

    @Builder
    public ResponseDto(int statusCode, String message, Object data){
        this.statusCode = statusCode;
        this.message = message;
        this.data = data;
    }
}
