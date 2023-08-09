package com.teamseven.MusicVillain;

public class ResponseObject {
    public int statusCode;
    public String message;
    public Object data;

    public ResponseObject(int statusCode, String message, Object data){
        this.statusCode = statusCode;
        this.message = message;
        this.data = data;
    }

    public ResponseObject(Status status, Object data){
        this.statusCode = status.getStatusCode();
        this.message = status.getMessage();
        this.data = data;
    }

    public static ResponseObject of(Status status, Object data){
        return new ResponseObject(status, data);
    }
    public static ResponseObject of(Status status){
        return new ResponseObject(status, null);
    }

}
