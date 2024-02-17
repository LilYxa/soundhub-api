package com.soundhub.api.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UuidGenerator;

import java.util.List;
import java.util.UUID;

@Entity
@Data
@NoArgsConstructor
@Table(name = "playlists")
public class Playlist {
    @Id
    @GeneratedValue
    @UuidGenerator(style = UuidGenerator.Style.TIME)
    private UUID id;

    @Column(name = "name")
    private String name;

    @OneToOne
    private User author;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "playlist")
    private List<Track> tracks;
}
