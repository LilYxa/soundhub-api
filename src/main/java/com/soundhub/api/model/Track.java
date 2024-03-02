package com.soundhub.api.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UuidGenerator;

import java.util.UUID;

@Entity
@Data
@NoArgsConstructor
@Table(name = "tracks")
public class Track {
    @Id
    @GeneratedValue
    @UuidGenerator(style = UuidGenerator.Style.TIME)
    private UUID id;

    @Column(name = "title")
    private String title;

    @Column(name = "duration")
    private String duration;

    @ManyToOne
    @JoinColumn(name = "album_id")
    private Album album;

}
