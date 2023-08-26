package com.teamseven.MusicVillain.Dto.ResponseBody;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;

public class ResponseObject extends ResponseEntity {

    /* ──────────────────────────── Fields ───────────────────────────── */

    /* Private Fields */
    private Status status; // -> This field does not need to be output to Response(JSON)

    /* Public Fields */
    public int statusCode;
    public String message;
    public Object data;
    /* ───────────────────────────────────────────────────────────────── */

    /* ──────────────────────── Generate Instance ────────────────────── */
    public static ResponseObject of(Status status, String message, Object data){
        // Customize message
        return new ResponseObject(status, message, data);
    }

    public static ResponseObject of(Status status, Object data){
        return new ResponseObject(status, data);
    }
    /* ─────────────────────────────────────────────────────────────── */

    /* ──────────────────────── Constructors ──────────────────────── */
    public ResponseObject(Status status){
        super(HttpStatusCode.valueOf(status.getStatusCode()));
        this.statusCode = status.getStatusCode();
        this.message = status.getMessage();
        this.data = null;
    }

    public ResponseObject(Status status, Object data){
        super(data ,HttpStatusCode.valueOf(status.getStatusCode()));
        this.statusCode = status.getStatusCode();
        this.message = status.getMessage();
        this.data = data;
    }

    // Customize message
    public ResponseObject(Status status, String message, Object data){

        super(data ,HttpStatusCode.valueOf(status.getStatusCode()));
        this.statusCode = status.getStatusCode();
        this.message = message;
        this.data = data;
    }
    /* ───────────────────────────────────────────────────────────────── */


    /* ──────────────────────── For Convenience ──────────────────────── */
    public static ResponseObject OK(Object data){
        return new ResponseObject(Status.OK, data);
    }

    public static ResponseObject BAD_REQUEST(Object data){
        return new ResponseObject(Status.BAD_REQUEST);
    }

    public static ResponseObject NOT_FOUND(){
        return new ResponseObject(Status.NOT_FOUND);
    }

    public static ResponseObject UNAUTHORIZED(){
        return new ResponseObject(Status.UNAUTHORIZED);
    }

    public static ResponseObject CONFLICT(){
        return new ResponseObject(Status.CONFLICT);
    }

    public static ResponseObject FORBIDDEN(){
        return new ResponseObject(Status.FORBIDDEN);
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
