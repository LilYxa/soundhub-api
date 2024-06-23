package com.soundhub.api.service.impl;

import com.soundhub.api.Constants;
import com.soundhub.api.exception.ApiException;
import com.soundhub.api.exception.PythonExecutionException;
import com.soundhub.api.service.RecommendationService;
import com.soundhub.api.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
public class RecommendationServiceImpl implements RecommendationService {

    @Autowired
    private UserService userService;

    @Override
    public List<UUID> getUsers(UUID user) {
        log.info("recommendUsers[1]: searching friends for user with id: {}", user);
        final String uri = Constants.PATH_TO_PYTHON_API+user;
        RestTemplate restTemplate = new RestTemplate();
        List<UUID> result = restTemplate.getForObject(uri, List.class);
        return result;
    }

    @Override
    public List<UUID> recommendUsers(UUID targetUser) {
        log.info("recommendUsers[1]: searching friends for user with id: {}", targetUser);
        try {
            return executePythonScript(targetUser);
        } catch (Exception e) {
            log.error("recommendUsers[2]: error: {}", e.getMessage());
            throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    private List<String> buildPythonCommand(UUID targetUser) {
        return Arrays.asList(Constants.PYTHON, Constants.PATH_TO_PYTHON_SCRIPT, targetUser.toString());
    }

    private List<UUID> executePythonScript(UUID targetUser) throws Exception {
        List<UUID> potentialFriends = new ArrayList<>();
        List<String> command = buildPythonCommand(targetUser);
        Process process = new ProcessBuilder(command).start();
        String line = "";
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            while ((line = reader.readLine()) != null) {
                potentialFriends.add(UUID.fromString(line));
            }
        } catch (Exception e) {
            log.error("executePythonScript[1]: error: {}", e.getMessage());
            throw new Exception(line);
        }
        int exitCode = process.waitFor();
        if (exitCode != 0) {
            log.error("executePythonScript[2]: Python script execution failed with exit code {}", exitCode);
            throw new PythonExecutionException(HttpStatus.INTERNAL_SERVER_ERROR, Constants.PYTHON_EXECUTION_FAILED);
        }
        return potentialFriends;
    }
}

