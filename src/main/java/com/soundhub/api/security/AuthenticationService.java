package com.soundhub.api.security;

import com.soundhub.api.Constants;
import com.soundhub.api.dto.*;
import com.soundhub.api.enums.Role;
import com.soundhub.api.exception.ApiException;
import com.soundhub.api.model.Artist;
import com.soundhub.api.model.User;
import com.soundhub.api.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final UserService userService;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    public AuthResponse signUp(SignUpDto signUpDto) {
        if (Boolean.TRUE.equals(userService.checkEmailAvailability(signUpDto.getEmail()))) {
            throw new ApiException(HttpStatus.BAD_REQUEST, Constants.USER_EMAIL_EXISTS_MSG);
        }

        if (Boolean.TRUE.equals(userService.checkUsernameAvailability(signUpDto.getUsername()))) {
            throw new ApiException(HttpStatus.BAD_REQUEST, Constants.USER_USERNAME_EXISTS_MSG);
        }

        var user = User.builder()
                .username(signUpDto.getUsername())
                .email(signUpDto.getEmail())
                .password(passwordEncoder.encode(signUpDto.getPassword()))
                .firstName(signUpDto.getFirstName())
                .lastName(signUpDto.getLastName())
                .birthday(signUpDto.getBirthday())
                .city(signUpDto.getCity())
                .country(signUpDto.getCountry())
                .gender(signUpDto.getGender())
                .avatarUrl(signUpDto.getAvatarUrl())
                .description(signUpDto.getDescription())
                .languages(signUpDto.getLanguages())
                .favoriteGenres(signUpDto.getFavoriteGenres())
                .favoriteArtists(signUpDto.getFavoriteArtists())
                .role(Role.ROLE_USER)
                .build();

        userService.addUser(user);
        var jwt = jwtService.generateToken(user);
        var refreshToken = refreshTokenService.createRefreshToken(user.getEmail());

        return AuthResponse.builder()
                .accessToken(jwt)
                .refreshToken(refreshToken.getRefreshToken())
                .build();
    }

    public AuthResponse signIn(SignInDto signInDto) {
//        authenticationManager.authenticate(
//                new UsernamePasswordAuthenticationToken(signInDto.getEmail(), signInDto.getPassword())
//        );
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(signInDto.getEmail(), signInDto.getPassword())
            );
        } catch (AuthenticationException e) {
            throw new ApiException(HttpStatus.UNAUTHORIZED, "Invalid email or password");
        }

        var user = userService.getUserByEmail(signInDto.getEmail());

        var jwt = jwtService.generateToken(user);
        var refreshToken = refreshTokenService.createRefreshToken(signInDto.getEmail());

        return AuthResponse.builder()
                .accessToken(jwt)
                .refreshToken(refreshToken.getRefreshToken())
                .build();
    }

    public AuthResponse refreshToken(RefreshTokenRequest request) {
        RefreshToken refreshToken = refreshTokenService.verifyRefreshToken(request.getRefreshToken());
        User user = refreshToken.getUser();
        String accessToken = jwtService.generateToken(user);

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken.getRefreshToken())
                .build();
    }

    public LogoutResponse logout(LogoutRequest request) {
        String jwt = jwtService.invalidateToken(request.getAccessToken());
        refreshTokenService.deleteRefreshToken(request.getRefreshToken());
        return new LogoutResponse(jwt);
    }

}
