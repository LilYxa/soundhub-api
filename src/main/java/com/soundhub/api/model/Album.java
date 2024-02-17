package com.soundhub.api.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Entity
@Data
@NoArgsConstructor
@Table(name = "albums")
public class Album {
    @Id
    @GeneratedValue
    @UuidGenerator(style = UuidGenerator.Style.TIME)
    private UUID id;

    @Column(name = "title")
    private String title;

    @Column(name = "releaseDate")
    private LocalDate releaseDate;

    @OneToMany(mappedBy = "album")
    private List<Track> tracks;

    @ManyToOne
    @JoinColumn(name = "artist_id")
    private Artist artist;

}
