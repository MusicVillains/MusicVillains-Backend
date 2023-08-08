package com.teamseven.MusicVillain;

import lombok.Getter;

@Getter
public enum Status {

    OK(200, "OK"),
    CREATED(201, "CREATED"),
    NO_CONTENT(204, "NO CONTENT"),
    BAD_REQUEST(400, "BAD REQUEST"),
    UNAUTHORIZED(401, "UNAUTHORIZED"),
    FORBIDDEN(403, "FORBIDDEN"),
    NOT_FOUND(404, "NOT FOUND"),
    CONFLICT(409, "CONFLICT"),
    CREATION_FAIL (400, "CREATION FAIL");
    private final int statusCode;
    private final String message;

    Status(int status, String message) {
        this.statusCode = status;
        this.message = message;
    }
}
