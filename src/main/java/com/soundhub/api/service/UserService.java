package com.soundhub.api.service;

import com.soundhub.api.model.User;

import java.util.UUID;

public interface UserService {
    User addUser(User user);

    User getUserById(UUID id);

    UUID deleteUser(String email);

    User updateUser(UUID id);

    User getUserByUsername(String username);
}
