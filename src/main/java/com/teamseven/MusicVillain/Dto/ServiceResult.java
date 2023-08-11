package com.teamseven.MusicVillain.Dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ServiceResult{
    public static final String SUCCESS = "success";
    public static final String FAIL = "fail";

    public String result;
    public String message;
    public Object data;

    public static ServiceResult of(String result, String message, Object data){
        return new ServiceResult(result, message, data);
    }

    public static ServiceResult of(String result, String message){
        return new ServiceResult(result, message, null);
    }

    public static ServiceResult success(Object data){
        return new ServiceResult(SUCCESS, null, data);
    }

    public static ServiceResult fail(String message){
        return new ServiceResult(FAIL, message, null);
    }

    public boolean isSuccessful(){
        return this.result.equals(SUCCESS);
    }

    public boolean isFailed(){
        return this.result.equals(FAIL);
    }

}
