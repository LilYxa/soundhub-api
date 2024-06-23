package com.soundhub.api.controller;

import com.soundhub.api.Constants;
import com.soundhub.api.dto.PostDto;
import com.soundhub.api.enums.Role;
import com.soundhub.api.exception.ApiException;
import com.soundhub.api.exception.ResourceNotFoundException;
import com.soundhub.api.model.Post;
import com.soundhub.api.model.User;
import com.soundhub.api.service.PostService;
import com.soundhub.api.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class PostControllerPositiveNegativeModuleTest {
    @InjectMocks
    private PostController postController;

    @Mock
    private PostService postService;

    @Mock
    private UserService userService;

    private UUID postId;
    private UUID authorId;
    private PostDto postDto;
    private Post post;
    private User user;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        postId = UUID.randomUUID();
        authorId = UUID.randomUUID();

        user = User.builder()
                .id(authorId)
                .email("vasya.pupkin@gmail.com")
                .password("testPassword")
                .firstName("Vasya")
                .lastName("Pupkin")
                .birthday(LocalDate.of(2000, 5, 15))
                .role(Role.ROLE_USER)
                .build();

        postDto = PostDto.builder()
                .id(postId)
                .author(user)
                .content("Test content")
                .build();

        post = Post.builder()
                .id(postId)
                .author(user)
                .content("Test content")
                .build();
    }

    @Test
    public void testGetPostById_Positive() {
        when(postService.getPostById(postId)).thenReturn(postDto);

        ResponseEntity<PostDto> response = postController.getPostById(postId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(postDto, response.getBody());
    }

    @Test
    public void testGetPostById_Negative() {
        when(postService.getPostById(postId)).thenThrow(new ResourceNotFoundException(Constants.POST_RESOURCE_NAME, Constants.ID_FIELD, postId));

        assertThrows(ResourceNotFoundException.class, () -> {
            ResponseEntity<PostDto> response = postController.getPostById(postId);
            assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        });

    }

    @Test
    public void testGetAllPostsByAuthor_Positive() {
        when(postService.getPostsByAuthor(authorId)).thenReturn(Collections.singletonList(post));

        ResponseEntity<List<Post>> response = postController.getAllPostsByAuthor(authorId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(1, response.getBody().size());
        assertEquals(post, response.getBody().get(0));
    }

    @Test
    public void testGetAllPostsByAuthor_Negative() {
        when(postService.getPostsByAuthor(authorId)).thenThrow(new ResourceNotFoundException(Constants.POST_RESOURCE_NAME, Constants.ID_FIELD, authorId));

        assertThrows(ResourceNotFoundException.class, () -> {
            ResponseEntity<List<Post>> response = postController.getAllPostsByAuthor(authorId);
            assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        });
    }

    @Test
    public void testAddPost_Positive() {
        when(postService.addPost(any(PostDto.class), any())).thenReturn(postDto);

        ResponseEntity<PostDto> response = postController.addPost(postDto, null);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(postDto, response.getBody());
    }

    @Test
    public void testAddPost_Negative() {
        assertThrows(ApiException.class, () -> {
            when(postService.addPost(any(PostDto.class), any())).thenThrow(new ApiException(HttpStatus.BAD_REQUEST, "Invalid data"));

            ResponseEntity<PostDto> response = postController.addPost(postDto, null);

            assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        });
    }

    @Test
    public void testToggleLike_Positive() {
        when(userService.getCurrentUser()).thenReturn(user);
        when(postService.toggleLike(postId, user)).thenReturn(post);

        ResponseEntity<Post> response = postController.toggleLike(postId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(post, response.getBody());
    }

    @Test
    public void testToggleLike_Negative() {
        when(userService.getCurrentUser()).thenReturn(user);
        when(postService.toggleLike(postId, user)).thenThrow(new ResourceNotFoundException(Constants.POST_RESOURCE_NAME, Constants.ID_FIELD, postId));

        assertThrows(ResourceNotFoundException.class, () -> {
            ResponseEntity<Post> response = postController.toggleLike(postId);
            assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        });
    }

    @Test
    public void testDeletePost_Positive() {
        when(postService.deletePost(postId)).thenReturn(postId);

        ResponseEntity<UUID> response = postController.deletePost(postId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(postId, response.getBody());
    }

    @Test
    public void testDeletePost_Negative() {
        when(postService.deletePost(postId)).thenThrow(new ResourceNotFoundException(Constants.POST_RESOURCE_NAME, Constants.ID_FIELD, postId));

        assertThrows(ResourceNotFoundException.class, () -> {
            ResponseEntity<UUID> response = postController.deletePost(postId);
            assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        });
    }
}
