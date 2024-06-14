package com.soundhub.api.controller;

import com.soundhub.api.dto.UserDto;
import com.soundhub.api.dto.request.CompatibleUsersRequest;
import com.soundhub.api.dto.response.CompatibleUsersResponse;
import com.soundhub.api.model.User;
import com.soundhub.api.service.RecommendationService;
import com.soundhub.api.service.UserService;
import com.soundhub.api.util.mappers.UserMapper;
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
    private UserMapper userMapper;

    @Autowired
    private RecommendationService recommendationService;

    @GetMapping("/{userId}")
    public ResponseEntity<UserDto> getUser(@PathVariable  UUID userId) {
        User user = userService.getUserById(userId);
        return new ResponseEntity<>(userMapper.userToUserDto(user), HttpStatus.OK);
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

    @PutMapping("/addFriend/{friendId}")
    public ResponseEntity<User> addFriend(@PathVariable UUID friendId) throws IOException {
        return ResponseEntity.ok(userService.addFriend(friendId));
    }

    @PutMapping("/deleteFriend/{friendId}")
    public ResponseEntity<User> deleteFriend(@PathVariable UUID friendId) throws IOException {
        return ResponseEntity.ok(userService.deleteFriend(friendId));
    }

    @GetMapping("/recommendedFriends")
    public ResponseEntity<List<User>> getRecommendedFriends() throws Exception {
        User currentUser = userService.getCurrentUser();
        List<User> potentialFriends = new ArrayList<>();
        List<UUID> ids = recommendationService.getUsers(currentUser.getId());
        //        List<UUID> ids = recommendationService.recommendUsers(currentUser.getId());
        List<User> rawFriends = userService.getUsersByIds(ids);
        rawFriends.forEach(friend -> {
            if (!currentUser.getFriends().contains(friend)) {
                potentialFriends.add(friend);
            }
        });
        return new ResponseEntity<>(potentialFriends, HttpStatus.OK);
    }

    @GetMapping("/{userId}/friends")
    public ResponseEntity<List<User>> getUserFriendsById(@PathVariable UUID userId) {
        return new ResponseEntity<>(userService.getUserFriendsById(userId), HttpStatus.OK);
    }

    @GetMapping("/search")
    public ResponseEntity<List<User>> searchUsersByFullName(@RequestParam String name) throws InterruptedException {
        List<User> users = userService.searchByFullName(name);
        if (users.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    @PutMapping("/toggleOnline")
    public ResponseEntity<User> toggleUserOnline() {
        return new ResponseEntity<>(userService.toggleUserOnline(), HttpStatus.OK);
    }

    @PostMapping("/compatibleUsers")
    public ResponseEntity<CompatibleUsersResponse> findCompatibilityPercentage(@RequestBody CompatibleUsersRequest requestBody) {
        return new ResponseEntity<>(userService.findCompatibilityPercentage(requestBody.getListUsersCompareWith()), HttpStatus.OK);
    }
}