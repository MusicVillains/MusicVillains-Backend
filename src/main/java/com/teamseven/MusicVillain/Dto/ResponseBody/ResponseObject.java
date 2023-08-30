package com.teamseven.MusicVillain.Dto.ResponseBody;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;

@Getter
@Setter
@Slf4j
public class ResponseObject extends ResponseEntity {
    /**
     * @Author: @Woody_K
     * @Date: 2023-08-26
     * @Description:
     *  Even if there is no data to be transmitted,
     *  the body is forcibly put into Response to keep the format statusCode, message, and data
     *  using `CustomResponseBody` class
     */


    /* ──────────────────────── Generate Instance ────────────────────── */
    public static ResponseObject of(Status status, String message, Object data){
        // Customize message
        return new ResponseObject(status, message, data);
    }

    public static ResponseObject of(Status status, Object data){
        return new ResponseObject(status, data);
    }

    public static ResponseObject of(Status status){
        return new ResponseObject(status);
    }
    /* ─────────────────────────────────────────────────────────────── */

    /* ──────────────────────── Constructors ──────────────────────── */
    public ResponseObject(Status status){

        super(CustomResponseBody.builder()
                .statusCode(status.getStatusCode())
                .message(status.getMessage())
                .data(null)
                .build(), HttpStatusCode.valueOf(status.getStatusCode()));
    }

    public ResponseObject(Status status, Object data){
        super(CustomResponseBody.builder()
                .statusCode(status.getStatusCode())
                .message(status.getMessage())
                .data(data)
                .build(), HttpStatusCode.valueOf(status.getStatusCode()));
    }

    // Customize message
    public ResponseObject(Status status, String message, Object data){

        super(CustomResponseBody.builder()
                .statusCode(status.getStatusCode())
                .message(message)
                .data(data)
                .build() ,HttpStatusCode.valueOf(status.getStatusCode()));
    }

    public ResponseObject(HttpHeaders headers, Status status, String message, Object data){
        super(CustomResponseBody.builder()
                .statusCode(status.getStatusCode())
                .message(message)
                .data(data)
                .build(), headers, HttpStatusCode.valueOf(status.getStatusCode()));
    }
    /* ───────────────────────────────────────────────────────────────── */


    /* ──────────────────────── For Convenience ──────────────────────── */
    public static ResponseObject OK(Object data){
        return new ResponseObject(Status.OK, data);
    }

    public static ResponseObject NO_CONTENT(){
        return new ResponseObject(Status.NO_CONTENT);
    }

    public static ResponseObject BAD_REQUEST(){
        return new ResponseObject(Status.BAD_REQUEST);
    }
    public static ResponseObject BAD_REQUEST(Object data){
        return new ResponseObject(Status.BAD_REQUEST, data);
    }

    public static ResponseObject NOT_FOUND(){
        return new ResponseObject(Status.NOT_FOUND);
    }

    public static ResponseObject UNAUTHORIZED(){
        return new ResponseObject(Status.UNAUTHORIZED);
    }

    public static ResponseObject UNAUTHORIZED(Object data){
        return new ResponseObject(Status.UNAUTHORIZED, data);
    }

    public static ResponseObject CONFLICT(){
        return new ResponseObject(Status.CONFLICT);
    }

    public static ResponseObject FORBIDDEN(){
        return new ResponseObject(Status.FORBIDDEN);
    }


    public static ResponseObject CREATED(){
        return new ResponseObject(Status.CREATED);
    }
    public static ResponseObject CREATED(Object data){
        return new ResponseObject(Status.CREATED, data);
    }

    public static ResponseObject CREATION_FAIL(){
        return new ResponseObject(Status.CREATION_FAIL);
    }
    public static ResponseObject CREATION_FAIL(Object data){
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
