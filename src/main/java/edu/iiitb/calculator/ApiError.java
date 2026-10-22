package edu.iiitb.calculator;

import java.time.OffsetDateTime;

public class ApiError {

    private String timestamp;
    private int status;
    private String error;
    private String path;

    public ApiError(int status, String error, String path) {
        this.timestamp = OffsetDateTime.now().toString();
        this.status = status;
        this.error = error;
        this.path = path;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public int getStatus() {
        return status;
    }

    public String getError() {
        return error;
    }

    public String getPath() {
        return path;
    }
}
