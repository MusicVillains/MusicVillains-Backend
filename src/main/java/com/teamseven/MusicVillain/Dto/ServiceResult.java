package com.teamseven.MusicVillain.Dto;

import io.swagger.v3.oas.annotations.media.Schema;
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

    /**
     * 외부에서 Constructor 사용할 수 없도록 private으로 선언
     * 메서드를 통해 객체 생성하도록 강제
     */

    private ServiceResult(){

    }
    private ServiceResult(String result, String message, Object data){
        this.result = result;
        this.message = message;
        this.data = data;
    }


    @Deprecated
    public static ServiceResult of(String result, String message, Object data){
        return new ServiceResult(result, message, data);
    }

    @Deprecated
    public static ServiceResult of(String result, String message){
        return new ServiceResult(result, message, null);
    }

    @Deprecated
    public static ServiceResult success(Object data){
        return new ServiceResult(SUCCESS, null, data);
    }

    @Deprecated
    public static ServiceResult fail(String message){
        return new ServiceResult(FAIL, message, null);
    }


    public static ServiceResult SUCCESS(){
        return new ServiceResult().success();
    }

    public static ServiceResult FAIL(){
        return new ServiceResult().fail();
    }

    public boolean isSuccessful(){
        return this.result.equals(SUCCESS);
    }

    public boolean isFailed(){
        return this.result.equals(FAIL);
    }

    public ServiceResult success(){
        this.result = SUCCESS;
        this.message = SUCCESS;
        return this;
    }

    public ServiceResult fail(){
        this.result = FAIL;
        this.message = FAIL;
        return this;
    }

    public ServiceResult message(String message){
        this.message = message;
        return this;
    }

    public ServiceResult data(Object data){
        this.data = data;
        return this;
    }

}
