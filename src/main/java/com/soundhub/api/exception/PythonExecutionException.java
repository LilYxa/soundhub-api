package com.soundhub.api.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class PythonExecutionException extends RuntimeException{

    private HttpStatus status;
    public PythonExecutionException(String message) {
        super(message);
    }

    public PythonExecutionException(HttpStatus httpStatus, String message) {
        super(message);
        this.status = httpStatus;
    }
}
