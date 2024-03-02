package com.soundhub.api.exception;

import lombok.Getter;

@Getter
public class RefreshTokenNotFoundException extends RuntimeException{
    private final String refreshToken;

    public RefreshTokenNotFoundException(String refreshToken) {
        super(String.format("Refresh token: %s not found", refreshToken));
        this.refreshToken = refreshToken;
    }
}
