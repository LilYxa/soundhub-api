package com.soundhub.api.service;

import com.soundhub.api.dto.UserDto;
import com.soundhub.api.model.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

public interface UserService {
//    User addUser(User user);
    User addUser(UserDto userDto, MultipartFile file) throws IOException;

    User addFriend(UUID friendId) throws IOException;

    User deleteFriend(UUID friendId) throws IOException;

    UserDto getUserById(UUID id);

    UUID deleteUser(UUID userId) throws IOException;

    UserDto updateUser(UUID userId, UserDto userDto) throws IOException;

    UserDto updateUser(UUID userId, UserDto userDto, MultipartFile file) throws IOException;

    User getUserByEmail(String email);

    Boolean checkEmailAvailability(String email);

    User getCurrentUser();
}
