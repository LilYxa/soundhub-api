package com.soundhub.api.controller;

import com.soundhub.api.Constants;
import com.soundhub.api.dto.*;
import com.soundhub.api.dto.request.RefreshTokenRequest;
import com.soundhub.api.dto.response.AuthResponse;
import com.soundhub.api.dto.response.LogoutResponse;
import com.soundhub.api.security.AuthenticationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {
    @Autowired
    private AuthenticationService authenticationService;

    @PostMapping("/sign-up")
    public ResponseEntity<AuthResponse> signUp(
            @RequestPart(name = "userData") @Valid UserDto userDto,
           @RequestPart(
               required = false,
               name = Constants.FILE_REQUEST_PART_ID
           ) MultipartFile file
    ) throws IOException {
        return ResponseEntity.ok(authenticationService.signUp(userDto, file));
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
