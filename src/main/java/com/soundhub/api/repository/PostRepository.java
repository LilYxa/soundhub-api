package com.soundhub.api.repository;

import com.soundhub.api.model.Post;
import com.soundhub.api.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface PostRepository extends JpaRepository<Post, UUID> {
    public List<Post> findAllByAuthor(User user);
}
