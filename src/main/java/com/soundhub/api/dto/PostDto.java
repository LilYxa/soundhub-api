package com.soundhub.api.dto;

import com.soundhub.api.model.User;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostDto {
    private UUID id;

    @NotNull
    private User author;

    private LocalDateTime publishDate;

    @NotBlank
    private String content;

    private List<String> images;

    private Set<User> likes;
}
