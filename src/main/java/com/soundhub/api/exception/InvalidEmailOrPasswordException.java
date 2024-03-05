package com.soundhub.api.exception;

public class InvalidEmailOrPasswordException extends RuntimeException{
    public InvalidEmailOrPasswordException(String message) {
        super(message);
    }
}
