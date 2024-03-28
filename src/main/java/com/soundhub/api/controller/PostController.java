package com.soundhub.api.controller;

import com.soundhub.api.dto.PostDto;
import com.soundhub.api.model.Post;
import com.soundhub.api.service.PostService;
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
public class PostController {
    @Autowired
    private PostService postService;
    @GetMapping("/{postId}")
    public ResponseEntity<PostDto> getPostById(@PathVariable UUID postId) {
        PostDto post = postService.getPostById(postId);
        return new ResponseEntity<>(post, HttpStatus.OK);

    }

    @GetMapping("/post/{authorId}")
    public ResponseEntity<List<Post>> getAllPostsByAuthor(@PathVariable UUID authorId) {
        return ResponseEntity.ok(postService.getPostsByAuthor(authorId));
    }

    @PostMapping("/add")
    public ResponseEntity<PostDto> addPost(@RequestPart PostDto postDto, @RequestPart List<MultipartFile> files) {
        return ResponseEntity.ok(postService.addPost(postDto, files));
    }

    @PutMapping("/update/{postId}")
    public ResponseEntity<PostDto> updatePost(@PathVariable UUID postId,
                                              @RequestPart PostDto postDto,
                                              @RequestPart(required = false) List<MultipartFile> files,
                                              @RequestPart(required = false) List<String> deleteFiles) throws IOException {
        return ResponseEntity.ok(postService.updatePost(postId, postDto, files, deleteFiles));
    }

    @DeleteMapping("/delete/{postId}")
    public ResponseEntity<UUID> deletePost(@PathVariable UUID postId) {
        return ResponseEntity.ok(postService.deletePost(postId));
    }
}
