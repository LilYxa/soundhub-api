package com.soundhub.api.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import com.soundhub.api.Constants;
import com.soundhub.api.enums.Gender;
import com.soundhub.api.enums.Role;
import com.soundhub.api.security.RefreshToken;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UuidGenerator;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.UUID;


@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "users")
@Builder
public class User implements UserDetails {
    @Id
    @GeneratedValue
    @UuidGenerator(style = UuidGenerator.Style.TIME)
    @Column(name = "id")
    private UUID id;

    @NotBlank
    @Column(name = "email", unique = true)
    private String email;

    @NotBlank
    @Column(name = "password")
    private String password;

    @NotBlank
    @Column(name = "firstName")
    private String firstName;

    @NotBlank
    @Column(name = "lastName")
    private String lastName;

    @NotNull
    @Column(name = "birthday")
    @JsonSerialize(using = LocalDateSerializer.class)
    private LocalDate birthday;

    @Column(name = "city")
    private String city;

    @Column(name = "country")
    private String country;

    @Enumerated(EnumType.STRING)
    @Column(name = "gender")
    private Gender gender;

    @Column(name = "avatarUrl")
    private String avatarUrl;

    @Column(name = "description")
    private String description;

    @ElementCollection(fetch = FetchType.EAGER)
    private List<String> languages;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_friends",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "friend_id"))
    @JsonIgnore
    private List<User> friends;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_favorite_genres",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "genre_id")
    )
    private List<Genre> favoriteGenres;

    @ElementCollection(fetch = FetchType.EAGER)
    private List<Integer> favoriteArtistsIds;

    @Column(name = "role")
    @Enumerated(value = EnumType.STRING)
    @JsonIgnore
    private Role role = Role.ROLE_USER;

    @OneToOne(mappedBy = "user")
    @JsonIgnore
    private RefreshToken refreshToken;

    @Column(name = "is_online")
    @NotNull
    private boolean isOnline = false;

    @Column(name = "last_online")
    @JsonSerialize(using = LocalDateTimeSerializer.class)
    @JsonFormat(pattern = Constants.LOCAL_DATETIME_FORMAT)
    private LocalDateTime lastOnline;

    public User(String email, String password,
                String firstName, String lastName, LocalDate birthday,
                String city, String country, Gender gender, String avatarUrl,
                String description, List<String> languages,
                List<Genre> favoriteGenres, List<Integer> favoriteArtists) {
        this.email = email;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.birthday = birthday;
        this.city = city;
        this.country = country;
        this.gender = gender;
        this.avatarUrl = avatarUrl;
        this.description = description;
        this.languages = languages;
        this.favoriteGenres = favoriteGenres;
        this.favoriteArtistsIds = favoriteArtists;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role.name()));
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", birthday=" + birthday +
                ", city='" + city + '\'' +
                ", country='" + country + '\'' +
                ", gender=" + gender +
                ", avatarUrl='" + avatarUrl + '\'' +
                ", description='" + description + '\'' +
                ", languages=" + languages +
                ", favoriteGenres=" + favoriteGenres +
                ", favoriteArtistsIds=" + favoriteArtistsIds +
                ", role=" + role +
                '}';
    }
}
