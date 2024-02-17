package com.soundhub.api.dto;

import com.soundhub.api.enums.Gender;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
public class UserDto {
    private UUID id;
    private String email;
    private String username;
    private String password;
    private String firstName;
    private String lastName;
    private LocalDate birthday;
    private String city;
    private String country;
    private Gender gender;
    private String avatarUrl;
    private String description;
    private List<String> languages;
    private List<UserDto> friends;
}
