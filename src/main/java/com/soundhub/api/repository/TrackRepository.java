package com.soundhub.api.repository;

import com.soundhub.api.model.Track;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface TrackRepository extends JpaRepository<Track, UUID> {
}
