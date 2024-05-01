package com.soundhub.api.util.mappers;

import com.soundhub.api.dto.UserDto;
import com.soundhub.api.model.User;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface UserMapper {
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateUserFromDto(UserDto userDto, @MappingTarget User entity);

//    @Mapping(target = "friends", ignore = true)
    UserDto userToUserDto(User user);

    User userDtoToUser(UserDto userDto);
}
