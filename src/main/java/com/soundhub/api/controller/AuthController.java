package com.soundhub.api.controller;

import com.soundhub.api.dto.*;
import com.soundhub.api.model.User;
import com.soundhub.api.security.AuthenticationService;
import com.soundhub.api.security.JwtService;
import com.soundhub.api.security.RefreshToken;
import com.soundhub.api.security.RefreshTokenService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthenticationService authenticationService;

    @PostMapping("/sign-up")
    public ResponseEntity<AuthResponse> signUp(@RequestBody @Valid SignUpDto signUpDto) {
        return ResponseEntity.ok(authenticationService.signUp(signUpDto));
    }

    @PostMapping("/sign-in")
    public ResponseEntity<AuthResponse> signIn(@RequestBody @Valid SignInDto signInDto) {
        return ResponseEntity.ok(authenticationService.signIn(signInDto));
    }

    @PostMapping("/logout")
    public ResponseEntity<LogoutResponse> logout(@RequestHeader("Authorization") String authHeader) {
        return ResponseEntity.ok(authenticationService.logout(authHeader));
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refreshToken(@RequestBody RefreshTokenRequest refreshTokenRequest) {
        return ResponseEntity.ok(authenticationService.refreshToken(refreshTokenRequest));
    }
}
