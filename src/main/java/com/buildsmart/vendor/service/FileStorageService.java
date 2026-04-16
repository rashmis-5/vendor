package com.buildsmart.vendor.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Path;

public interface FileStorageService {

    String storeFile(MultipartFile file, String subDirectory) throws IOException;

    void deleteFile(String filePath);

    Path getFilePath(String relativePath);
}
