package com.soundhub.api.service;


import com.soundhub.api.model.Artist;

import java.util.List;

public interface ArtistService {
    Artist addArtist(Artist artist);

    List<Artist> getAllArtists();
}
