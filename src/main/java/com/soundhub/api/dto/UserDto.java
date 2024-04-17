package com.soundhub.api.dto;

import com.soundhub.api.enums.Gender;
import com.soundhub.api.model.Genre;
import com.soundhub.api.model.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDto {
    private UUID id;
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

    private String city;
    private String country;
    private Gender gender;
    private String avatarUrl;
    private String description;
    private List<String> languages;
    private List<User> friends;
    private List<Genre> favoriteGenres;
    private List<Integer> favoriteArtistsIds;
}
