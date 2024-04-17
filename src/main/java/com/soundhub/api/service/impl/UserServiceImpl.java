package com.soundhub.api.service.impl;

import com.soundhub.api.Constants;
import com.soundhub.api.dto.UserDto;
import com.soundhub.api.enums.Role;
import com.soundhub.api.exception.ApiException;
import com.soundhub.api.exception.ResourceNotFoundException;
import com.soundhub.api.model.User;
import com.soundhub.api.repository.UserRepository;
import com.soundhub.api.service.FileService;
import com.soundhub.api.service.UserService;
import com.soundhub.api.util.UserMapper;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.soundhub.api.Constants.FRIEND_ALREADY_ADDED;

@Service
@Slf4j
public class UserServiceImpl implements UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FileService fileService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserMapper userMapper;

    @Value("${project.avatar}")
    private String path;

    @Value("${base.url}")
    private String baseUrl;

    @Override
    public User addUser(UserDto userDto, MultipartFile file) throws IOException {
//        String avatarUrl = baseUrl + Constants.FILE_PATH_PART + Constants.DEFAULT_USER_AVATAR_NAME;
//
//        if (file != null) {
//            if (Files.exists(Paths.get(path + File.separator + file.getOriginalFilename()))) {
//                throw new ApiException(HttpStatus.BAD_REQUEST, Constants.FILE_ALREADY_EXISTS);
//            }
//
//            String uploadedFilename = fileService.uploadFile(path, file);
//            avatarUrl = baseUrl + Constants.FILE_PATH_PART + uploadedFilename;
//        }
        String uploadedFilename = (file == null) ? null : fileService.uploadFile(path, file);
        String avatarUrl = (uploadedFilename == null) ? null : baseUrl + Constants.FILE_PATH_PART + uploadedFilename;

        User user = User.builder()
                .email(userDto.getEmail())
                .password(passwordEncoder.encode(userDto.getPassword()))
                .firstName(userDto.getFirstName())
                .lastName(userDto.getLastName())
                .birthday(userDto.getBirthday())
                .city(userDto.getCity())
                .country(userDto.getCountry())
                .gender(userDto.getGender())
                .avatarUrl(avatarUrl)
                .description(userDto.getDescription())
                .languages(userDto.getLanguages())
                .favoriteGenres(userDto.getFavoriteGenres())
                .favoriteArtistsIds(userDto.getFavoriteArtistsIds())
                .role(Role.ROLE_USER)
                .build();

        return userRepository.save(user);
    }

    @Override
    public User addFriend(UUID friendId) throws IOException {
        User user = getCurrentUser();
        User newFriend = getUserById(friendId);
        user.getFriends().add(newFriend);
        log.info("addFriend[1]: Friend added successfully ID {}", friendId);
        newFriend.getFriends().add(user);
        log.info("addFriend[2]: Friends list {}", user.getFriends());
        userRepository.save(user);
        userRepository.save(newFriend);
        return user;
    }

    @Override
    public User deleteFriend(UUID friendId) throws IOException {
        User user = getCurrentUser();
        User delFriend = userRepository.findById(friendId)
                .orElseThrow(() -> new ResourceNotFoundException(Constants.USER_RESOURCE_NAME, Constants.ID_FIELD, friendId));
        user.getFriends().remove(delFriend);
        log.info("deleteFriend[1]: Friend deleted successfully ID {}", friendId);
        updateUser(user.getId(), userMapper.userToUserDto(user));
        return user;
    }

    @Override
    public User getUserById(UUID id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(Constants.USER_RESOURCE_NAME, Constants.ID_FIELD, id));
    }

    @Override
    public UUID deleteUser(UUID userId) throws IOException {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException(Constants.USER_RESOURCE_NAME, Constants.ID_FIELD, userId));

        String fileName = user.getAvatarUrl().substring(user.getAvatarUrl().lastIndexOf("/") + 1);
        Files.deleteIfExists(Paths.get(path + File.separator + fileName));

        userRepository.delete(user);
        return user.getId();
    }

    @Override
    public UserDto updateUser(UUID userId, UserDto userDto) throws IOException {
        User user = userRepository.findById(userId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(Constants.USER_RESOURCE_NAME, Constants.ID_FIELD, userId));
        userMapper.updateUserFromDto(userDto, user);
        userRepository.save(user);
        return userMapper.userToUserDto(user);
    }

    @Override
    public UserDto updateUser(UUID userId, UserDto userDto, MultipartFile file) throws IOException {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException(Constants.USER_RESOURCE_NAME, Constants.ID_FIELD, userId));

        String fileName = user.getAvatarUrl() == null ? null : user.getAvatarUrl().substring(user.getAvatarUrl().lastIndexOf("/") + 1);
        if (file != null) {
            Files.deleteIfExists(Paths.get(path + File.separator + fileName));
            fileName = fileService.uploadFile(path, file);
        }
        String avatarUrl = baseUrl + Constants.FILE_PATH_PART + fileName;

        userMapper.updateUserFromDto(userDto, user);
        user.setAvatarUrl(avatarUrl);

        userRepository.save(user);

        return userMapper.userToUserDto(user);
    }

    @Override
    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email).orElseThrow(() -> new ResourceNotFoundException(Constants.USER_RESOURCE_NAME, Constants.EMAIL_FIELD, email));
    }

    @Override
    public Boolean checkEmailAvailability(String email) {
        return userRepository.existsByEmail(email);
    }

    @Override
    public User getCurrentUser() {
        var username = SecurityContextHolder.getContext().getAuthentication().getName();
        return getUserByEmail(username);
    }

    @Override
    public List<User> getUsersByIds(List<UUID> ids) {
        return userRepository.findByUserIds(ids);
    }

    @Override
    public List<User> getUserFriendsById(UUID id) {
        log.info("getUserFriendsById[1]: getting user's: {} friends", id);
        User user = getUserById(id);
        log.info("getUserFriendsById[2]: user: {}", user);
        log.info("getUserFriendsById[3]: user's friends: {}", user.getFriends());
        return user.getFriends();
    }
}
