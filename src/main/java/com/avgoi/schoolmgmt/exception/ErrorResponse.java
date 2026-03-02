package com.avgoi.schoolmgmt.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ErrorResponse {

    private Instant timestamp;
    private int status;
    private String error;
    private String message;

    public static ErrorResponse of(int status, String message) {
        ErrorResponse r = new ErrorResponse();
        r.setTimestamp(Instant.now());
        r.setStatus(status);
        r.setError(status == 400 ? "Bad Request" : status == 404 ? "Not Found" : status == 500 ? "Internal Server Error" : "Error");
        r.setMessage(message);
        return r;
    }
}
