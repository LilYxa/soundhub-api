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

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
@Slf4j
public class RecommendationServiceImpl implements RecommendationService {

    @Autowired
    private UserService userService;

    @Override
    public List<UUID> recommendUsers(UUID targetUser) {
        log.info("recommendUsers[1]: searching friends for user with id: {}", targetUser);
        List<UUID> potentialFriends = new ArrayList<>();
        int neigh = 3;

        try {
            List<String> command = buildExecutionCommand(targetUser, neigh);
            ProcessBuilder pb = new ProcessBuilder(command);
            Process process = pb.start();

            // Получаем вывод скрипта
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                potentialFriends.add(UUID.fromString(line));
            }

            // Ждем завершения процесса
            int exitCode = process.waitFor();
            if (exitCode != 0) {
                log.error("recommendUsers[1]: error during python execution");
                throw new PythonExecutionException(HttpStatus.INTERNAL_SERVER_ERROR, Constants.PYTHON_EXECUTION_FAILED);
            }
        } catch (Exception e) {
            log.error("recommendUsers[2]: error: {}", e.getMessage());
            throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }

        return potentialFriends;
    }

    private List<String> buildExecutionCommand(UUID targetUserId, int neigh) {
        List<String> command = new ArrayList<>();
        String scriptPath = Objects.requireNonNull(
                RecommendationServiceImpl.class
                    .getClassLoader()
                    .getResource("recommend.py")
        )
        .getPath();

        command.add("python3.8");
        command.add(scriptPath);

        // Первый аргумент - UUID пользователя
        command.add(targetUserId.toString());
        // Второй аргумент - количество соседей
        command.add(Integer.toString(neigh));

        return command;
    }
}

