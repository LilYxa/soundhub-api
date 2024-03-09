package com.soundhub.api.dto;

import com.soundhub.api.enums.Gender;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
public class UserDto {
    private UUID id;
    @NotBlank
    private String email;

    @NotBlank
    private String password;

    @NotBlank
    private String firstName;

    @NotBlank
    private String lastName;

    @NotNull
    private LocalDate birthday;
    private String city;
    private String country;
    private Gender gender;
    private String avatarUrl;
    private String description;
    private List<String> languages;
    private List<UserDto> friends;
}
