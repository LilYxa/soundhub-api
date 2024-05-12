package com.soundhub.api.controller;

import com.soundhub.api.dto.PostDto;
import com.soundhub.api.model.Post;
import com.soundhub.api.service.PostService;
import com.soundhub.api.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/posts")
@Slf4j
public class PostController {
    @Autowired
    private PostService postService;

    @Autowired
    private UserService userService;
    @GetMapping("/{postId}")
    public ResponseEntity<PostDto> getPostById(@PathVariable UUID postId) {
        PostDto post = postService.getPostById(postId);
        log.info("getPostById[1] controller: Post has successfully found by ID: {}", postId);
        return new ResponseEntity<>(post, HttpStatus.OK);
    }

    @GetMapping("/post/{authorId}")
    public ResponseEntity<List<Post>> getAllPostsByAuthor(@PathVariable UUID authorId) {
        log.info("getAllPostsByAuthor[1] controller: Find posts by author ID: {}", authorId);
        return ResponseEntity.ok(postService.getPostsByAuthor(authorId));
    }

    @PostMapping("/add")
    public ResponseEntity<PostDto> addPost(@RequestPart PostDto postDto,
                                           @RequestPart(required = false) List<MultipartFile> files) {
        log.info("addPost[1] controller: Post added");
        return ResponseEntity.ok(postService.addPost(postDto, files));
    }

    @PutMapping("/update/{postId}")
    public ResponseEntity<PostDto> updatePost(@PathVariable UUID postId,
                                              @RequestPart PostDto postDto,
                                              @RequestPart(required = false, name = "files") List<MultipartFile> files,
                                              @RequestPart(required = false, name = "deleteFiles") List<String> deleteFiles) throws IOException {
        if ((files == null) && (deleteFiles == null)) {
            log.info("updatePost[1] controller: Post updated without files, post ID: {}", postId);
            return ResponseEntity.ok(postService.updatePost(postId, postDto));
        } else {
            log.info("updatePost[1] controller: Post updated with files add/replace, post ID: {}", postId);
            return ResponseEntity.ok(postService.updatePost(postId, postDto, files, deleteFiles));
        }
    }

    @PutMapping("/like/{postId}")
    public ResponseEntity<Post> toggleLike(@PathVariable UUID postId) {
        log.info("toggleLike[1] controller: Like added to post ID: {}", postId);
        return ResponseEntity.ok(postService.toggleLike(postId, userService.getCurrentUser()));
    }

    @DeleteMapping("/delete/{postId}")
    public ResponseEntity<UUID> deletePost(@PathVariable UUID postId) {
        log.info("deletePost[1] controller: Post deleted post ID: {}", postId);
        return ResponseEntity.ok(postService.deletePost(postId));
    }
}
