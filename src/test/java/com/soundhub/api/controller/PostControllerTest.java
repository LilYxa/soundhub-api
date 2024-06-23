package com.soundhub.api.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.soundhub.api.dto.PostDto;
import com.soundhub.api.enums.Role;
import com.soundhub.api.model.Post;
import com.soundhub.api.model.User;
import com.soundhub.api.security.JwtService;
import com.soundhub.api.service.PostService;
import com.soundhub.api.service.UserService;
import com.soundhub.api.util.mappers.PostMapper;
import lombok.extern.slf4j.Slf4j;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;

import static org.mockito.Mockito.*;

import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMultipartHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;
import java.util.Collections;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
@Slf4j
public class PostControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private PostService postService;

    @MockBean
    private PostMapper postMapper;

    @MockBean
    private UserService userService;

    @InjectMocks
    private PostController postController;

    private UUID postId;
    private UUID authorId;
    private PostDto postDto;
    private PostDto postDtoUpd;
    private Post post;
    private User user;
    private String jwtToken;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        this.mockMvc = MockMvcBuilders.standaloneSetup(postController).build();

        postId = UUID.randomUUID();
        authorId = UUID.randomUUID();

//        User mockUser = Mockito.mock(User.class);

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

        postDtoUpd = PostDto.builder()
                .id(postId)
                .author(user)
                .content("Test content")
                .build();

        post = Post.builder()
                .id(postId)
                .author(user)
                .content("Test content")
                .build();

        jwtToken = "Bearer " + jwtService.generateToken(user);
        log.debug("setUp[1]: jwtToken {}", jwtToken);
    }


    @Test
    @WithMockUser
    public void testAddPost() throws Exception {
        MockMultipartFile postDtoFile = new MockMultipartFile("postDto", "", "application/json", new ObjectMapper().writeValueAsBytes(postDto));
        MockMultipartFile image1 = new MockMultipartFile("files", "image1.jpg", "image/jpeg", "image1 content".getBytes());
        MockMultipartFile image2 = new MockMultipartFile("files", "image2.jpg", "image/jpeg", "image2 content".getBytes());

        Mockito.when(postService.addPost(Mockito.any(PostDto.class), Mockito.anyList())).thenReturn(postDto);

        mockMvc.perform(MockMvcRequestBuilders.multipart("/api/v1/posts/add")
                        .file(postDtoFile)
                        .file(image1)
                        .file(image2)
                        .header("Authorization", jwtToken)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(postId.toString()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.content").value(postDto.getContent()));

        verify(postService, times(1)).addPost(any(PostDto.class), anyList());
    }

    @Test
    @WithMockUser
    public void testGetPostByIdWithToken() throws Exception {
        when(postService.getPostById(postId)).thenReturn(postDto);

        mockMvc.perform(get("/api/v1/posts/{postId}", postId)
                        .header("Authorization", jwtToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(postId.toString()))
                .andExpect(jsonPath("$.content").value("Test content"));
    }

    @Test
    public void testGetPostById() throws Exception {
        when(postService.getPostById(postId)).thenReturn(postDto);

        ResponseEntity<PostDto> response = postController.getPostById(postId);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(postDto, response.getBody());

        verify(postService, times(1)).getPostById(postId);
    }

    @Test
    @WithMockUser
    public void testGetAllPostsByAuthor() throws Exception {
        when(postService.getPostsByAuthor(authorId)).thenReturn(Collections.singletonList(post));

        mockMvc.perform(get("/api/v1/posts/post/{authorId}", authorId)
                        .header("Authorization", jwtToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(postId.toString()));
    }

    @Test
    @WithMockUser
    public void testUpdatePost() throws Exception {
        MockMultipartFile postDtoFile = new MockMultipartFile("postDto", "", "application/json", new ObjectMapper().writeValueAsBytes(postDto));
        MockMultipartFile image1 = new MockMultipartFile("files", "image1.jpg", "image/jpeg", "image1 content".getBytes());
        MockMultipartFile image2 = new MockMultipartFile("files", "image2.jpg", "image/jpeg", "image2 content".getBytes());

        when(postService.updatePost(any(), any(), any(), any())).thenReturn(postDtoUpd);

        MockMultipartHttpServletRequestBuilder builder =
                MockMvcRequestBuilders.multipart("/api/v1/posts/update/{postId}", postId);
        builder.with(new RequestPostProcessor() {
            @Override
            public MockHttpServletRequest postProcessRequest(MockHttpServletRequest request) {
                request.setMethod("PUT");
                return request;
            }
        });
        MvcResult mvcResult = mockMvc.perform(builder
                        .file(postDtoFile)
                        .file(image1)
                        .file(image2)
                        .header("Authorization", "Bearer " + jwtToken)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(postId.toString()))
                .andExpect(MockMvcResultMatchers.jsonPath("$.content").value(postDtoUpd.getContent()))
                .andReturn();
        log.debug("mvcResult \"{}\"", mvcResult.getResponse().getContentAsString());
    }

    @Test
    @WithMockUser
    public void testToggleLike() throws Exception {
        when(userService.getCurrentUser()).thenReturn(user);
        when(postService.toggleLike(postId, user)).thenReturn(post);

        mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/posts/like/{postId}", postId)
                        .header("Authorization", jwtToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(postId.toString()));
    }

    @Test
    @WithMockUser
    public void testDeletePost() throws Exception {
        when(postService.deletePost(postId)).thenReturn(postId);

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/posts/delete/{postId}", postId)
                        .header("Authorization", jwtToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(postId.toString()));
    }
}
