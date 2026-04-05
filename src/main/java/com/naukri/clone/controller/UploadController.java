package com.naukri.clone.controller;

import com.naukri.clone.model.User;
import com.naukri.clone.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;

@Controller
public class UploadController {

    @Autowired
    private UserService userService;

    private static final String UPLOAD_DIR =
        System.getProperty("user.home") + "/naukri-uploads/resumes/";

    @PostMapping("/profile/upload-resume")
    public String uploadResume(
            @RequestParam("resumeFile") MultipartFile file,
            Authentication authentication) throws IOException {

        // 1. Empty file check
        if (file.isEmpty()) {
            return "redirect:/profile?resumeError=empty";
        }

        // 2. File type validation
        String contentType = file.getContentType();
        if (contentType == null ||
            (!contentType.equals("application/pdf") &&
             !contentType.contains("word") &&
             !contentType.contains("document"))) {
            return "redirect:/profile?resumeError=type";
        }

        // 3. Get logged-in user
        User user = userService.findByEmail(authentication.getName())
                               .orElseThrow(() -> new RuntimeException("User not found"));

        // 4. Create upload directory if not exists
        Files.createDirectories(Paths.get(UPLOAD_DIR));

        // 5. Build unique filename
        String originalName = file.getOriginalFilename();
        String fileName = "resume_" + user.getId() + "_"
                        + System.currentTimeMillis() + "_" + originalName;

        // 6. Save file to disk
        Path dest = Paths.get(UPLOAD_DIR + fileName);
        Files.copy(file.getInputStream(), dest, StandardCopyOption.REPLACE_EXISTING);

        // ✅ FIX: Use correct field names from User model
        user.setResumeFileName(fileName);           // was: setResumeUrl()  ❌
        user.setResumeOriginalName(originalName);   // store original name for display

        userService.updateUser(user);

        return "redirect:/profile?resumeUploaded=true";
    }
}
