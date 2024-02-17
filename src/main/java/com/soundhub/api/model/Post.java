package com.soundhub.api.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Data
@NoArgsConstructor
@Table(name = "posts")
public class Post {
    @Id
    @GeneratedValue
    @UuidGenerator(style = UuidGenerator.Style.TIME)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User author;

    @Column(name = "publishDate")
    private LocalDateTime publishDate;

    @Column(name = "content")
    private String content;

    @ElementCollection
    private List<String> images;

}
