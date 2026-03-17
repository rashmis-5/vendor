package com.buildsmart.vendor.service;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.nio.file.*;
import java.util.UUID;
@Service
public class FileStorageService {
    @Value("${file.upload-dir:./uploads}")
    private String uploadDir;
    private static final long MAX_FILE_SIZE = 20 * 1024 * 1024;
    private static final String[] ALLOWED_TYPES = {
        "application/pdf","image/jpeg","image/png","image/jpg",
        "application/msword",
        "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
        "application/vnd.ms-excel",
        "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
    };
    public String storeFile(MultipartFile file, String subDirectory) throws IOException {
        if (file.isEmpty()) throw new IllegalArgumentException("File is empty");
        if (file.getSize() > MAX_FILE_SIZE) throw new IllegalArgumentException("File exceeds 20MB limit");
        validateFileType(file.getContentType());
        Path uploadPath = Paths.get(uploadDir, subDirectory).toAbsolutePath().normalize();
        Files.createDirectories(uploadPath);
        String originalFilename = file.getOriginalFilename();
        String extension = (originalFilename != null && originalFilename.contains("."))
                ? originalFilename.substring(originalFilename.lastIndexOf(".")) : "";
        String uniqueFilename = UUID.randomUUID() + extension;
        Path targetPath = uploadPath.resolve(uniqueFilename);
        Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);
        return Paths.get(subDirectory, uniqueFilename).toString();
    }
    public void deleteFile(String filePath) {
        try { Files.deleteIfExists(Paths.get(uploadDir, filePath).toAbsolutePath().normalize()); }
        catch (IOException ignored) {}
    }
    public Path getFilePath(String relativePath) {
        return Paths.get(uploadDir, relativePath).toAbsolutePath().normalize();
    }
    private void validateFileType(String contentType) {
        if (contentType == null) throw new IllegalArgumentException("Cannot determine file type");
        for (String allowed : ALLOWED_TYPES) {
            if (allowed.equalsIgnoreCase(contentType)) return;
        }
        throw new IllegalArgumentException("File type not allowed. Allowed: PDF, JPG, PNG, DOC, DOCX, XLS, XLSX");
    }
}
