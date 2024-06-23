package com.soundhub.api.service;

import com.soundhub.api.dto.PostDto;
import com.soundhub.api.enums.Role;
import com.soundhub.api.exception.ResourceNotFoundException;
import com.soundhub.api.model.Post;
import com.soundhub.api.model.User;
import com.soundhub.api.repository.PostRepository;
import com.soundhub.api.repository.UserRepository;
import com.soundhub.api.service.impl.PostServiceImpl;
import com.soundhub.api.util.mappers.PostMapper;
import com.soundhub.api.util.mappers.UserMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PostServiceTest {

    @InjectMocks
    private PostServiceImpl postService;

    @Mock
    private PostRepository postRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserService userService;

    @Mock
    private FileService fileService;

    @Mock
    private UserMapper userMapper;

    @Mock
    private PostMapper postMapper;

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
                .likes(Set.of(user))
                .build();
    }

    @Test
    public void testAddPost_Positive() {
        when(userService.getCurrentUser()).thenReturn(user);
        when(postRepository.save(any(Post.class))).thenReturn(post);
        when(postMapper.toPostDto(any(Post.class))).thenReturn(postDto);

        PostDto result = postService.addPost(postDto, null);

        assertEquals(postDto, result);
        verify(postRepository, times(1)).save(any(Post.class));
    }

    @Test
    public void testAddPost_Negative() {
        when(userService.getCurrentUser()).thenReturn(user);
        when(postRepository.save(any(Post.class))).thenThrow(new RuntimeException("Database error"));

        assertThrows(RuntimeException.class, () -> postService.addPost(postDto, null));
    }

//    @Test
//    public void testToggleLike_Positive() {
//        when(postRepository.findById(postId)).thenReturn(Optional.of(post));
//        when(postRepository.save(any(Post.class))).thenReturn(post);
//
//        Post result = postService.toggleLike(postId, user);
//
//        assertEquals(post, result);
//        verify(postRepository, times(1)).save(any(Post.class));
//    }

    @Test
    public void testToggleLike_Negative() {
        when(postRepository.findById(postId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> postService.toggleLike(postId, user));
    }

    @Test
    public void testGetPostById_Positive() {
        when(postRepository.findById(postId)).thenReturn(Optional.of(post));
        when(postMapper.toPostDto(any(Post.class))).thenReturn(postDto);

        PostDto result = postService.getPostById(postId);

        assertEquals(postDto, result);
    }

    @Test
    public void testGetPostById_Negative() {
        when(postRepository.findById(postId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> postService.getPostById(postId));
    }

//    @Test
//    public void testDeletePost_Positive() {
//        when(postRepository.findById(postId)).thenReturn(Optional.of(post));
//        when(userService.getCurrentUser()).thenReturn(user);
//
//        UUID result = postService.deletePost(postId);
//
//        assertEquals(postId, result);
//        verify(postRepository, times(1)).delete(any(Post.class));
//    }

    @Test
    public void testDeletePost_Negative() {
        when(postRepository.findById(postId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> postService.deletePost(postId));
    }

    @Test
    public void testUpdatePost_Positive() {
        when(postRepository.findById(postId)).thenReturn(Optional.of(post));
        when(userService.getCurrentUser()).thenReturn(user);
        when(postMapper.toPostDto(any(Post.class))).thenReturn(postDto);

        PostDto result = postService.updatePost(postId, postDto);

        assertEquals(postDto, result);
        verify(postRepository, times(1)).save(any(Post.class));
    }

    @Test
    public void testUpdatePost_Negative() {
        when(postRepository.findById(postId)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> postService.updatePost(postId, postDto));
    }

    @Test
    public void testGetPostsByAuthor_Positive() {
        when(userService.getUserById(authorId)).thenReturn(user);
        when(postRepository.findAllByAuthor(any(User.class))).thenReturn(Collections.singletonList(post));

        List<Post> result = postService.getPostsByAuthor(authorId);

        assertEquals(1, result.size());
        assertEquals(post, result.get(0));
    }

    @Test
    public void testGetPostsByAuthor_Negative() {
        when(userService.getUserById(authorId)).thenReturn(user);
        when(postRepository.findAllByAuthor(any(User.class))).thenReturn(Collections.emptyList());

        List<Post> result = postService.getPostsByAuthor(authorId);

        assertEquals(0, result.size());
    }
}
