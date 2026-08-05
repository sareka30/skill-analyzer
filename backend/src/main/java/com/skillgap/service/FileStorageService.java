package com.skillgap.service;

import com.skillgap.exception.BadRequestException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class FileStorageService {

    @Value("${app.upload.dir}")
    private String uploadDir;

    private Path resolveUploadPath() {
        Path path = Paths.get(uploadDir).toAbsolutePath().normalize();
        try {
            Files.createDirectories(path);
        } catch (IOException e) {
            throw new BadRequestException("Could not create upload directory: " + e.getMessage());
        }
        return path;
    }

    /**
     * Stores the uploaded file on disk under a random, collision-free name
     * while preserving the original extension, and returns the stored path.
     */
    public String store(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BadRequestException("Uploaded file is empty");
        }

        String originalFilename = StringUtils.cleanPath(
                file.getOriginalFilename() == null ? "resume" : file.getOriginalFilename());
        String extension = "";
        int dotIndex = originalFilename.lastIndexOf('.');
        if (dotIndex >= 0) {
            extension = originalFilename.substring(dotIndex).toLowerCase();
        }

        if (!extension.equals(".pdf") && !extension.equals(".docx")) {
            throw new BadRequestException("Only .pdf and .docx resume files are supported");
        }

        String storedName = UUID.randomUUID() + extension;
        Path targetPath = resolveUploadPath().resolve(storedName);

        try (InputStream in = file.getInputStream()) {
            Files.copy(in, targetPath, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new BadRequestException("Failed to store file: " + e.getMessage());
        }

        return targetPath.toString();
    }
}
