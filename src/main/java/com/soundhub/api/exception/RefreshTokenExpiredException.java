package com.soundhub.api.exception;

import com.soundhub.api.security.RefreshToken;
import lombok.Getter;

@Getter
public class RefreshTokenExpiredException extends RuntimeException{
    private final RefreshToken refreshToken;

    public RefreshTokenExpiredException(RefreshToken refreshToken) {
        super(String.format("Refresh token: %s expired", refreshToken.getRefreshToken()));
        this.refreshToken = refreshToken;
    }
}
