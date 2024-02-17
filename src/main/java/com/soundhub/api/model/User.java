package com.soundhub.api.model;

import com.soundhub.api.enums.Gender;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;


@Entity
@Data
@NoArgsConstructor
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue
    @UuidGenerator(style = UuidGenerator.Style.TIME)
    @Column(name = "id")
    private UUID id;

    @NotBlank
    @Column(name = "email", unique = true)
    private String email;

    @NotBlank
    @Column(name = "username")
    private String username;

    @NotBlank
    @Column(name = "password")
    private String password;

    @NotBlank
    @Column(name = "firstName")
    private String firstName;

    @NotBlank
    @Column(name = "lastName")
    private String lastName;

    @NotBlank
    @Column(name = "birthday")
    private LocalDate birthday;

    @NotBlank
    @Column(name = "city")
    private String city;

    @NotBlank
    @Column(name = "country")
    private String country;

    @NotBlank
    @Enumerated(EnumType.STRING)
    @Column(name = "gender")
    private Gender gender;

    @NotBlank
    @Column(name = "avatarUrl")
    private String avatarUrl;

    @NotBlank
    @Column(name = "description")
    private String description;

    @ElementCollection
    private List<String> languages;

    @OneToMany
    private List<User> friends;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "userRole",
            joinColumns = @JoinColumn(name = "userId", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "roleId", referencedColumnName = "id")
    )
    private List<Role> roles;

}
