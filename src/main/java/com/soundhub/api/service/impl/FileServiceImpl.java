package com.soundhub.api.service.impl;

import com.soundhub.api.exception.ApiException;
import com.soundhub.api.service.FileService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
public class FileServiceImpl implements FileService {
    @Value("${project.resources.path}")
    private String resourcesPath;

    @Value("${project.staticFolder}")
    private String staticFolder;

    @Override
    public String uploadFile(String path, MultipartFile multipartFile) throws IOException {
        String fileName = multipartFile.getOriginalFilename();

        String uuidFilename = UUID.randomUUID() + "_" + fileName;
        File fileFolder = getStaticPath(path).toFile();

        Path filePath = Paths.get(fileFolder.getAbsolutePath(), uuidFilename);

        log.debug("uploadFile[1]: {}", resourcesPath);
        log.debug("uploadFile[2]: {}", fileFolder);

        if (!fileFolder.exists()) {
            fileFolder.mkdir();
        }

        Files.copy(multipartFile.getInputStream(), filePath);
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
        String staticPath = getStaticPath(path).toFile().getAbsolutePath();
        String filePath = Paths.get(staticPath, filename).toFile().getAbsolutePath();
        return new FileInputStream(filePath);
    }

    @Override
    public Path getStaticPath(String path) {
        return Paths.get(resourcesPath, staticFolder, path);
    }

    @Override
    public Path getStaticFile(String folder, String filename) {
        String staticPath = getStaticPath(folder)
                .toFile()
                .getAbsolutePath();

        return Paths.get(staticPath, filename);
    }
}
