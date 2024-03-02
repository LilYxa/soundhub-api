package com.soundhub.api.service.impl;

import com.soundhub.api.model.Artist;
import com.soundhub.api.repository.ArtistRepository;
import com.soundhub.api.service.ArtistService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ArtistServiceImpl implements ArtistService {

    @Autowired
    private ArtistRepository artistRepository;

    @Override
    public Artist addArtist(Artist artist) {
        return artistRepository.save(artist);
    }


}
