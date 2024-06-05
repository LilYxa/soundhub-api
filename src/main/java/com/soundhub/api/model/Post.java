package com.soundhub.api.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.soundhub.api.Constants;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "posts")
@Builder
public class Post {
    @Id
    @GeneratedValue
    @UuidGenerator(style = UuidGenerator.Style.TIME)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User author;

    @Column(name = "publishDate")
    @JsonFormat(pattern = Constants.LOCAL_DATETIME_FORMAT)
    private LocalDateTime publishDate;

    @Column(name = "content")
    private String content;

    @ElementCollection
    private List<String> images;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_posts",
            joinColumns = @JoinColumn(name = "post_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id"))
    private Set<User> likes = new HashSet<>();
}
