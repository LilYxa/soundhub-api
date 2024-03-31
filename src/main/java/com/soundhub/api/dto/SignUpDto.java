package com.soundhub.api.dto;

import com.soundhub.api.enums.Gender;
import com.soundhub.api.model.Genre;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Data
public class SignUpDto {

    @NotBlank
    @Size(max = 50)
    @Email
    private String email;

    @NotBlank
    @Size(min = 8, max = 30)
    private String password;

    @NotBlank
    @Size(min = 2, max = 20)
    private String firstName;

    @NotBlank
    @Size(min = 2, max = 20)
    private String lastName;

    @NotNull
    private LocalDate birthday;

    @Size(min = 2, max = 20)
    private String city;

    @Size(min = 2, max = 20)
    private String country;

    private Gender gender;

    private String avatarUrl;

    private String description;

    private List<String> languages;

    private List<Genre> favoriteGenres;

    private List<Integer> favoriteArtistsIds;
}
