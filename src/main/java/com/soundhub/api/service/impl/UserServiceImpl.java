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
import java.util.Optional;
import java.util.UUID;

@Service
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

//    @Override
//    public User addUser(User user) {
//        return userRepository.save(user);
//    }

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
                .favoriteArtists(userDto.getFavoriteArtists())
                .role(Role.ROLE_USER)
                .build();

        return userRepository.save(user);
    }

    @Override
    public UserDto getUserById(UUID id) {
        return toUserDto(userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(Constants.USER_RESOURCE_NAME, Constants.ID_FIELD, id)));
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
    public UserDto updateUser(UUID userId, UserDto userDto, MultipartFile file) throws IOException {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException(Constants.USER_RESOURCE_NAME, Constants.ID_FIELD, userId));

        String fileName = user.getAvatarUrl() == null ? null : user.getAvatarUrl().substring(user.getAvatarUrl().lastIndexOf("/") + 1);
        if (file != null) {
            Files.deleteIfExists(Paths.get(path + File.separator + fileName));
            fileName = fileService.uploadFile(path, file);
        }
        String avatarUrl = baseUrl + Constants.FILE_PATH_PART + fileName;

//        user.setEmail(userDto.getEmail());
//        user.setPassword(passwordEncoder.encode(userDto.getPassword()));
//        user.setFirstName(userDto.getFirstName());
//        user.setLastName(userDto.getLastName());
//        user.setBirthday(userDto.getBirthday());
//        user.setCity(userDto.getCity());
//        user.setCountry(userDto.getCountry());
//        user.setGender(userDto.getGender());
//        user.setAvatarUrl(avatarUrl);
//        user.setDescription(userDto.getDescription());
//        user.setLanguages(userDto.getLanguages());
//        user.setFavoriteGenres(userDto.getFavoriteGenres());
//        user.setFavoriteArtists(userDto.getFavoriteArtists());

        userMapper.updateUserFromDto(userDto, user);
        user.setAvatarUrl(avatarUrl);

        userRepository.save(user);

        return toUserDto(user);
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

    private UserDto toUserDto(User user) {
        return UserDto.builder()
                .id(user.getId())
                .email(user.getEmail())
                .password(passwordEncoder.encode(user.getPassword()))
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .birthday(user.getBirthday())
                .city(user.getCity())
                .country(user.getCountry())
                .gender(user.getGender())
                .avatarUrl(user.getAvatarUrl())
                .description(user.getDescription())
                .languages(user.getLanguages())
                .favoriteGenres(user.getFavoriteGenres())
                .favoriteArtists(user.getFavoriteArtists())
                .build();
    }
}
