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
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
            // Строим команду для выполнения Python-скрипта
            List<String> command = new ArrayList<>();
            command.add("python3"); // Используем команду "python3"
            command.add("src/main/resources/recommend.py"); // Путь к Python-скрипту

            // Добавляем аргументы командной строки
            command.add(targetUser.toString()); // Первый аргумент - UUID пользователя
            command.add(Integer.toString(neigh)); // Второй аргумент - количество соседей

            // Создаем процесс с помощью ProcessBuilder
            ProcessBuilder pb = new ProcessBuilder(command);
            Process process = pb.start();

//            Pattern pattern = Pattern.compile("'(.*?)'");

            // Получаем вывод скрипта
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
//                Matcher matcher = pattern.matcher(line);
//
//                while (matcher.find()) {
//                    potentialFriends.add(UUID.fromString(matcher.group(1)));
//                }
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
}

