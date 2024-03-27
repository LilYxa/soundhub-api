package com.soundhub.api.service.impl;

import com.soundhub.api.model.Genre;
import com.soundhub.api.repository.GenreRepository;
import com.soundhub.api.service.GenreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GenreServiceImpl implements GenreService {
    @Autowired
    private GenreRepository genreRepository;

    @Override
    public List<Genre> getAllGenres() {
        return genreRepository.findAll();
    }
}
