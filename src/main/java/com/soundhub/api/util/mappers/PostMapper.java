package com.soundhub.api.util.mappers;

import com.soundhub.api.dto.PostDto;
import com.soundhub.api.model.Post;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface PostMapper {
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updatePostFromDto(PostDto postResponse, @MappingTarget Post entity);

    PostDto toPostDto(Post post);

    Post dtoToPost(PostDto postDto);
}
