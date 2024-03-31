package com.soundhub.api.controller;

import com.soundhub.api.dto.UserDto;
import com.soundhub.api.model.User;
import com.soundhub.api.service.RecommendationService;
import com.soundhub.api.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {
    @Autowired
    private UserService userService;

    @Autowired
    private RecommendationService recommendationService;

    @GetMapping("/{userId}")
    public ResponseEntity<UserDto> getUser(@PathVariable  UUID userId) {
        UserDto user = userService.getUserById(userId);
        return new ResponseEntity<>(user, HttpStatus.OK);
    }

    @PutMapping("/update/{userId}")
    public ResponseEntity<UserDto> updateUser(@PathVariable UUID userId,
                                              @RequestPart UserDto userDto,
                                              @RequestPart(required = false) MultipartFile file) throws IOException {
        return ResponseEntity.ok(userService.updateUser(userId, userDto, file));
    }

    @DeleteMapping("/delete/{userId}")
    public ResponseEntity<UUID> deleteUser(@PathVariable UUID userId) throws IOException {
        return ResponseEntity.ok(userService.deleteUser(userId));
    }

    @GetMapping("/currentUser")
    public ResponseEntity<User> getCurrentUser() {
        User currentUser = userService.getCurrentUser();
        return new ResponseEntity<>(currentUser, HttpStatus.OK);
    }

    @GetMapping("/{userId}/recommendedFriends")
    public ResponseEntity<List<User>> getRecommendedFriends(@PathVariable UUID userId) {
        User currentUser = userService.getCurrentUser();
        List<User> potentialFriends = new ArrayList<>();
        List<UUID> ids = recommendationService.recommendUsers(userId);
        List<User> rawFriends = userService.getUsersByIds(ids);
        rawFriends.forEach(friend -> {
            if (!currentUser.getFriends().contains(friend)) {
                potentialFriends.add(friend);
            }
        });
        return new ResponseEntity<>(potentialFriends, HttpStatus.OK);
    }
}