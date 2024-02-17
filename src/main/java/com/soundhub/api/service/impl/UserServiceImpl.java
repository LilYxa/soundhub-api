package com.soundhub.api.service.impl;

import com.soundhub.api.Constants;
import com.soundhub.api.exception.ResourceNotFoundException;
import com.soundhub.api.model.User;
import com.soundhub.api.repository.UserRepository;
import com.soundhub.api.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserRepository userRepository;

    @Override
    public User addUser(User user) {
        return userRepository.save(user);
    }

    @Override
    public User getUserById(UUID id) {
        return userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException(Constants.USER_RESOURCE_NAME, Constants.ID_FIELD, id));
    }

    @Override
    public UUID deleteUser(String email) {
        return null;

    }

    @Override
    public User updateUser(UUID id) {
        return null;
    }

    @Override
    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username).orElseThrow(() -> new ResourceNotFoundException(Constants.USER_RESOURCE_NAME, Constants.USERNAME_FIELD, username));
    }
}
