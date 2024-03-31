package com.soundhub.api.service.impl;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.regex.Pattern;

public class Test {
    public static void main(String[] args) {
        List<UUID> potentialFriends = new ArrayList<>();
        UUID targetUser = UUID.fromString("7f000101-8e67-1b84-818e-674bc1530000");
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

            Pattern pattern = Pattern.compile("'(.*?)'");


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
                throw new Exception("Python script execution failed");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Печатаем список потенциальных друзей
        System.out.println(potentialFriends);
    }
}
