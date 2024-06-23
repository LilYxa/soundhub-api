package com.soundhub.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.soundhub.api.Constants;
import com.soundhub.api.dto.PostDto;
import com.soundhub.api.dto.SignInDto;
import com.soundhub.api.dto.SignUpDto;
import com.soundhub.api.dto.UserDto;
import com.soundhub.api.dto.request.RefreshTokenRequest;
import com.soundhub.api.dto.response.AuthResponse;
import com.soundhub.api.dto.response.LogoutResponse;
import com.soundhub.api.enums.Gender;
import com.soundhub.api.enums.Role;
import com.soundhub.api.model.Genre;
import com.soundhub.api.model.User;
import com.soundhub.api.security.AuthenticationService;
import com.soundhub.api.security.BlacklistingService;
import com.soundhub.api.security.JwtService;
import com.soundhub.api.security.RefreshTokenService;
import com.soundhub.api.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
@Slf4j
public class AuthControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtService jwtService;
    @MockBean
    private UserService userService;
    @MockBean
    private BlacklistingService blacklistingService;
    @MockBean
    private RefreshTokenService refreshTokenService;
    @MockBean
    private AuthenticationManager authenticationManager;

    @MockBean
    private AuthenticationService authenticationService;

    @InjectMocks
    private AuthController authController;

    private User user;
    private UserDto userDto;
    private SignUpDto signUpDto;
    private SignInDto signInDto;
    private AuthResponse authResponse;
    private LogoutResponse logoutResponse;
    private String accessToken, refreshToken;
    private RefreshTokenRequest refreshTokenRequest;

    @BeforeEach
    public void setUp() throws IOException {
        MockitoAnnotations.openMocks(this);
        this.mockMvc = MockMvcBuilders.standaloneSetup(authController).build();

//        MockMultipartFile avatar = new MockMultipartFile("avatar", "image1.jpg", "image/jpeg", "image1 content".getBytes());

        user = User.builder()
                .id(UUID.randomUUID())
                .email("vasya.pupkin@gmail.com")
                .password("testPassword")
                .firstName("Vasya")
                .lastName("Pupkin")
                .birthday(LocalDate.of(2000, 5, 15))
                .role(Role.ROLE_USER)
                .build();

        userDto = UserDto.builder()
                .id(UUID.randomUUID())
                .email("vasya.pupkin@gmail.com")
                .password("testPassword")
                .firstName("Vasya")
                .lastName("Pupkin")
                .birthday(LocalDate.of(2000, 5, 15))
                .isOnline(false)
                .build();

        signUpDto = SignUpDto.builder()
                .email("vasya.pupkin@gmail.com")
                .password("testPassword")
                .firstName("Vasya")
                .lastName("Pupkin")
                .birthday(LocalDate.of(2000, 5, 15))
                .build();

        signInDto = SignInDto.builder()
                .email("vasya.pupkin@gmail.com")
                .password("testPassword")
                .build();

        accessToken = jwtService.generateToken(user);
        refreshToken = UUID.randomUUID().toString();
        log.debug("setUp[1]: access&refresh token: {} , {}", accessToken, refreshToken);

        authResponse = AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();

        logoutResponse = LogoutResponse.builder()
                .message(Constants.SUCCESSFUL_LOGOUT)
                .build();

        refreshTokenRequest = RefreshTokenRequest.builder()
                .refreshToken(refreshToken)
                .build();
    }

    @Test
    public void testSignUp() throws Exception {
        when(authenticationService.signUp(any(UserDto.class), any(MultipartFile.class))).thenReturn(authResponse);

        MockMultipartFile userDtoFile = new MockMultipartFile("userData", "", "application/json", new ObjectMapper().writeValueAsBytes(userDto));
        MockMultipartFile avatar = new MockMultipartFile("file", "image1.jpg", "image/jpeg", "image1 content".getBytes());

        MvcResult result = mockMvc.perform(multipart("/api/v1/auth/sign-up")
                        .file(userDtoFile)
                        .file(avatar)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value(authResponse.getAccessToken()))
                .andExpect(jsonPath("$.refreshToken").value(authResponse.getRefreshToken()))
                .andReturn();

        log.debug("testSignUp[1]: response result = {}", result.getResponse().getContentAsString());

        verify(authenticationService, times(1)).signUp(any(UserDto.class), any(MultipartFile.class));
    }

    @Test
    void testSignIn() throws Exception {
        when(authenticationService.signIn(any(SignInDto.class))).thenReturn(authResponse);

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/auth/sign-in")
                        .content(new ObjectMapper().writeValueAsString(signInDto))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value(authResponse.getAccessToken()))
                .andExpect(jsonPath("$.refreshToken").value(authResponse.getRefreshToken()))
                .andReturn();

        log.debug("testSignIn[1]: response result = {}", result.getResponse().getContentAsString());

        verify(authenticationService, times(1)).signIn(any(SignInDto.class));
    }

    @Test
    void testLogout() throws Exception {
        String authHeader = "Bearer " + accessToken;

        when(authenticationService.logout(authHeader)).thenReturn(logoutResponse);

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/auth/logout")
                        .header("Authorization", authHeader))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.message").value(logoutResponse.getMessage()))
                .andReturn();

        log.debug("testLogout[1]: response result = {}", result.getResponse().getContentAsString());

        verify(authenticationService, times(1)).logout(any(String.class));
    }

    @Test
    void testRefreshToken() throws Exception {
        when(authenticationService.refreshToken(any(RefreshTokenRequest.class))).thenReturn(authResponse);

        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/auth/refresh")
                        .content(new ObjectMapper().writeValueAsString(refreshTokenRequest))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").value(authResponse.getAccessToken()))
                .andExpect(jsonPath("$.refreshToken").value(authResponse.getRefreshToken()))
                .andReturn();

        log.debug("testRefreshToken[1]: response result = {}", result.getResponse().getContentAsString());

        verify(authenticationService, times(1)).refreshToken(any(RefreshTokenRequest.class));
    }
}
