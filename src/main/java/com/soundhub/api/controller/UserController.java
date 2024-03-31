package com.soundhub.api.controller;

import com.soundhub.api.dto.UserDto;
import com.soundhub.api.model.User;
import com.soundhub.api.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {
    @Autowired
    private UserService userService;

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

    @PutMapping("/addFriend/{friendId}")
    public ResponseEntity<User> addFriend(@PathVariable UUID friendId) throws IOException {
        return ResponseEntity.ok(userService.addFriend(friendId));
    }

    @PutMapping("/deleteFriend/{friendId}")
    public ResponseEntity<User> deleteFriend(@PathVariable UUID friendId) throws IOException {
        return ResponseEntity.ok(userService.deleteFriend(friendId));
    }
}
