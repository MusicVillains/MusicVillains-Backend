package com.teamseven.MusicVillain.Dto.ResponseBody;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;

@Getter
@Setter
@Slf4j
public class ResponseObject extends ResponseEntity {
    /**
     * Even if there is no data to be transmitted,
     * the body is forcibly put into Response to keep the format statusCode, message, and data
     * using `CustomResponseBody` class <br><br>
     * - `useCustomizedResponse` 옵션을 사용하기 위해 기존 생성자를 private로 선언하여, of 메소드로만 생성할 수 있도록 강제함.
     * @author @Woody K
     * @date 2023-08-26
     * @see CustomResponseBody
     */

    /* ──────────────────────── Option ────────────────────── */

    /* WARN: test needed */
    /* CUSTOM_RESPONSE_BODY_MODE
       - if true: use customized response body(class CustomResponseBody),
       - if false: use raw data as response body */
    private static boolean customResponseBodyMode = true;


    /* ──────────────────────── Generate Instance ────────────────────── */
    public static ResponseObject onlyData(Status status, Object data){
        return new ResponseObject(data, status);
    }

    public static ResponseObject of(Status status, String message, Object data){
        if(!customResponseBodyMode)
            /* The message received as a parameter is ignored and raw data is entered in the body */
            return new ResponseObject(data, status);

        // Customize message
        return new ResponseObject(status, message, data);
    }

    public static ResponseObject of(Status status, Object data){
        if(!customResponseBodyMode)
            /* The message received as a parameter is ignored and raw data is entered in the body */
            return new ResponseObject(data, status);

        return new ResponseObject(status, data);
    }

    public static ResponseObject of(Status status){
        if(!customResponseBodyMode)
            /* The message received as a parameter is ignored and raw data is entered in the body */
            return new ResponseObject("", status);

        return new ResponseObject(status);
    }
    /* ─────────────────────────────────────────────────────────────── */

    /* ──────────────────────── Constructors ──────────────────────── */
    private ResponseObject(Status status){
        super(CustomResponseBody.builder()
                .statusCode(status.getStatusCode())
                .message(status.getMessage())
                .data(null)
                .build(), HttpStatusCode.valueOf(status.getStatusCode()));
    }

    private ResponseObject(Status status, Object data){
        super(CustomResponseBody.builder()
                .statusCode(status.getStatusCode())
                .message(status.getMessage())
                .data(data)
                .build(), HttpStatusCode.valueOf(status.getStatusCode()));
    }

    // Customize message
    private ResponseObject(Status status, String message, Object data){
        super(CustomResponseBody.builder()
                .statusCode(status.getStatusCode())
                .message(message)
                .data(data)
                .build() ,HttpStatusCode.valueOf(status.getStatusCode()));
    }

    private ResponseObject(Object responseBody, Status status){
        super(responseBody, HttpStatusCode.valueOf(status.getStatusCode()));
    }
    private ResponseObject(HttpHeaders headers, Status status, String message, Object data){
        super(CustomResponseBody.builder()
                .statusCode(status.getStatusCode())
                .message(message)
                .data(data)
                .build(), headers, HttpStatusCode.valueOf(status.getStatusCode()));
    }
    /* ───────────────────────────────────────────────────────────────── */


    /* ──────────────────────── For Convenience ──────────────────────── */
    public static ResponseObject OK(Object data){
        if(!customResponseBodyMode)
            /* The message received as a parameter is ignored and raw data is entered in the body */
            return new ResponseObject(data, Status.OK);
        return new ResponseObject(Status.OK, data);
    }

    public static ResponseObject NO_CONTENT(){
        if(!customResponseBodyMode)
            /* The message received as a parameter is ignored and raw data is entered in the body */
            return new ResponseObject(Status.NO_CONTENT.getMessage(), Status.NO_CONTENT);
        return new ResponseObject(Status.NO_CONTENT);
    }

    public static ResponseObject BAD_REQUEST(){
        if(!customResponseBodyMode)
            /* The message received as a parameter is ignored and raw data is entered in the body */
            return new ResponseObject(Status.BAD_REQUEST.getMessage(), Status.BAD_REQUEST);
        return new ResponseObject(Status.BAD_REQUEST);
    }
    public static ResponseObject BAD_REQUEST(Object data){
        if(!customResponseBodyMode)
            /* The message received as a parameter is ignored and raw data is entered in the body */
            return new ResponseObject(data, Status.BAD_REQUEST);
        return new ResponseObject(Status.BAD_REQUEST, data);
    }

    public static ResponseObject NOT_FOUND(){
        if(!customResponseBodyMode)
            /* The message received as a parameter is ignored and raw data is entered in the body */
            return new ResponseObject(Status.NOT_FOUND.getMessage(), Status.NOT_FOUND);
        return new ResponseObject(Status.NOT_FOUND);
    }

    public static ResponseObject UNAUTHORIZED(){
        if(!customResponseBodyMode)
            /* The message received as a parameter is ignored and raw data is entered in the body */
            return new ResponseObject(Status.UNAUTHORIZED.getMessage(), Status.UNAUTHORIZED);
        return new ResponseObject(Status.UNAUTHORIZED);
    }

    public static ResponseObject UNAUTHORIZED(Object data){
        if(!customResponseBodyMode)
            /* The message received as a parameter is ignored and raw data is entered in the body */
            return new ResponseObject(data, Status.UNAUTHORIZED);

        return new ResponseObject(Status.UNAUTHORIZED, data);
    }

    public static ResponseObject CONFLICT(){
        if(!customResponseBodyMode)
            /* The message received as a parameter is ignored and raw data is entered in the body */
            return new ResponseObject(Status.CONFLICT.getMessage(), Status.CONFLICT);
        return new ResponseObject(Status.CONFLICT);
    }

    public static ResponseObject FORBIDDEN(){
        if(!customResponseBodyMode)
            /* The message received as a parameter is ignored and raw data is entered in the body */
            return new ResponseObject(Status.FORBIDDEN.getMessage(), Status.FORBIDDEN);
        return new ResponseObject(Status.FORBIDDEN);
    }


    public static ResponseObject CREATED(){
        if(!customResponseBodyMode)
            /* The message received as a parameter is ignored and raw data is entered in the body */
            return new ResponseObject(Status.CREATED.getMessage(), Status.CREATED);
        return new ResponseObject(Status.CREATED);
    }
    public static ResponseObject CREATED(Object data){
        if(!customResponseBodyMode)
            /* The message received as a parameter is ignored and raw data is entered in the body */
            return new ResponseObject(data, Status.CREATED);
        return new ResponseObject(Status.CREATED, data);
    }

    public static ResponseObject CREATION_FAIL(){
        if(!customResponseBodyMode)
            /* The message received as a parameter is ignored and raw data is entered in the body */
            return new ResponseObject(Status.CREATION_FAIL.getMessage(), Status.CREATION_FAIL);
        return new ResponseObject(Status.CREATION_FAIL);
    }
    public static ResponseObject CREATION_FAIL(Object data){
        if(!customResponseBodyMode)
            /* The message received as a parameter is ignored and raw data is entered in the body */
            return new ResponseObject(data, Status.CREATION_FAIL);
        return new ResponseObject(Status.CREATION_FAIL, data);
    }

    /* ─────────────────────────────────────────────────────────── */


    /* ──────────────────────── Override ──────────────────────── */


    public ResponseObject(HttpStatusCode status) {
        super(status);
    }

    public ResponseObject(Object body, HttpStatusCode status) {
        super(body, status);
    }

    @Override
    public HttpStatusCode getStatusCode() {
        return super.getStatusCode();
    }

    @Override
    public int getStatusCodeValue() {
        return super.getStatusCodeValue();
    }

    @Override
    public HttpHeaders getHeaders() {
        return super.getHeaders();
    }

    @Override
    public Object getBody() {
        return super.getBody();
    }

    @Override
    public boolean hasBody() {
        return super.hasBody();
    }

    @Override
    public boolean equals(Object other) {
        return super.equals(other);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public String toString() {
        return super.toString();
    }

    public ResponseObject(Object body, MultiValueMap headers, int rawStatus) {
        super(body, headers, rawStatus);
    }

    public ResponseObject(Object body, MultiValueMap headers, HttpStatusCode status) {
        super(body, headers, status);
    }

    public ResponseObject(MultiValueMap headers, HttpStatusCode status) {
        super(headers, status);
    }
}
