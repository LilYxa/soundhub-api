package com.soundhub.api.service;

import com.soundhub.api.dto.PostDto;
import com.soundhub.api.model.Post;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

public interface PostService {
    public PostDto addPost(PostDto postDto, List<MultipartFile> files);

//    public PostDto addLike(UUID postId);

    public PostDto getPostById(UUID postId);

    public UUID deletePost(UUID postId);

    public PostDto updatePost(UUID postId, PostDto postDto, List<MultipartFile> files, List<String> replaceFilesUrls) throws IOException;

    public List<Post> getPostsByAuthor(UUID authorId);
}
