package com.naukri.clone.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.UUID;

@Service
public class FileUploadService {

    @Value("${app.upload.dir:uploads}")
    private String uploadDir;

    //RESUME UPLOAD
    public String saveResume(MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) return null;
        String ext = getExtension(file.getOriginalFilename());
        if (!ext.equalsIgnoreCase(".pdf") && !ext.equalsIgnoreCase(".doc") && !ext.equalsIgnoreCase(".docx")) {
            throw new IllegalArgumentException("Only PDF and DOC files allowed!");
        }
        String storedName = UUID.randomUUID() + ext;
        Path path = Paths.get(uploadDir, "resumes");
        Files.createDirectories(path);
        Files.copy(file.getInputStream(), path.resolve(storedName), StandardCopyOption.REPLACE_EXISTING);
        return storedName;
    }

    //PROFILE PHOTO UPLOAD
    public String saveProfilePhoto(MultipartFile file) throws IOException {
        if (file == null || file.isEmpty()) return null;
        String ext = getExtension(file.getOriginalFilename());
        if (!ext.equalsIgnoreCase(".jpg") && !ext.equalsIgnoreCase(".jpeg")
                && !ext.equalsIgnoreCase(".png") && !ext.equalsIgnoreCase(".webp")) {
            throw new IllegalArgumentException("Only JPG, PNG, WEBP images allowed!");
        }
        String storedName = UUID.randomUUID() + ext;
        Path path = Paths.get(uploadDir, "photos");
        Files.createDirectories(path);
        Files.copy(file.getInputStream(), path.resolve(storedName), StandardCopyOption.REPLACE_EXISTING);
        return storedName;
    }

    public Path getResumePath(String fileName) {
        return Paths.get(uploadDir, "resumes", fileName);
    }

    public Path getPhotoPath(String fileName) {
        return Paths.get(uploadDir, "photos", fileName);
    }

    private String getExtension(String filename) {
        if (filename != null && filename.contains(".")) {
            return filename.substring(filename.lastIndexOf("."));
        }
        return "";
    }
}
