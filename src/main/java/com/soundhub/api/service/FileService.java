package com.soundhub.api.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.List;

public interface FileService {
    String uploadFile(String path, MultipartFile multipartFile) throws IOException;

    List<String> uploadFileList(String path, List<MultipartFile> multipartFile);

    InputStream getResourceFile(String path, String filename) throws FileNotFoundException;

    Path getStaticPath(String folder);

    Path getStaticFile(String folder, String filename);
}
