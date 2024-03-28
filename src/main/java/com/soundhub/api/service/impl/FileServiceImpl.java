package com.soundhub.api.service.impl;

import com.soundhub.api.exception.ApiException;
import com.soundhub.api.service.FileService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class FileServiceImpl implements FileService {
    @Override
    public String uploadFile(String path, MultipartFile multipartFile) throws IOException {
        String fileName = multipartFile.getOriginalFilename();

        String uuidFilename = UUID.randomUUID() + "_" + fileName;

        String filePath = path + File.separator + uuidFilename;

        File file = new File(path);
        if (!file.exists()) {
            file.mkdir();
        }

        Files.copy(multipartFile.getInputStream(), Paths.get(filePath));
        return uuidFilename;
    }

    @Override
    public List<String> uploadFileList(String path, List<MultipartFile> multipartFile) {
        List<String> names = new ArrayList<>();
        multipartFile.parallelStream().forEach(file -> {
            try {
                names.add(uploadFile(path, file));
            } catch (IOException e) {
                throw new ApiException(HttpStatus.BAD_REQUEST, e.getMessage());
            }
        });
        return names;
    }

    @Override
    public InputStream getResourceFile(String path, String filename) throws FileNotFoundException {
        String filePath = path + File.separator + filename;
        return new FileInputStream(filePath);
    }
}
