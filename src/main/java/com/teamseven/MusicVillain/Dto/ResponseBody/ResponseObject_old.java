package com.teamseven.MusicVillain.Dto.ResponseBody;


@Deprecated
public class ResponseObject_old {
    /**
     * @Deprecated:
     *  This class is deprecated. Use {@link ResponseObject} instead.
     */

    public int statusCode;
    public String message;
    public Object data;

    public ResponseObject_old(int statusCode, String message, Object data){
        this.statusCode = statusCode;
        this.message = message;
        this.data = data;
    }

    public ResponseObject_old(Status status, Object data){
        this.statusCode = status.getStatusCode();
        this.message = status.getMessage();
        this.data = data;
    }
    public static ResponseObject_old of(Status status, String message, Object data){
        return new ResponseObject_old(status.getStatusCode(), message, data);
    }
    public static ResponseObject_old of(Status status, Object data){
        return new ResponseObject_old(status, data);
    }
    public static ResponseObject_old of(Status status){
        return new ResponseObject_old(status, null);
    }

    public static ResponseObject_old authenticationFail(){
        return new ResponseObject_old(Status.AUTHENTICATION_FAIL.getStatusCode(),
                Status.AUTHENTICATION_FAIL.getMessage(), null);
    }

    public static ResponseObject_old unauthorized(){
        return new ResponseObject_old(Status.UNAUTHORIZED.getStatusCode(),
                Status.UNAUTHORIZED.getMessage(), null);
    }
}
