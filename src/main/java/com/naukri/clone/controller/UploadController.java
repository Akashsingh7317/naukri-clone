package com.naukri.clone.controller;

import com.naukri.clone.model.User;
import com.naukri.clone.service.FileUploadService;
import com.naukri.clone.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class UploadController {

    @Autowired private UserService userService;
    @Autowired private FileUploadService fileUploadService;

    // Resume upload via /profile/upload-resume (alternative endpoint)
    @PostMapping("/profile/upload-resume")
    public String uploadResume(
            @RequestParam("resumeFile") MultipartFile file,
            Authentication authentication,
            RedirectAttributes ra) {
        if (file == null || file.isEmpty()) {
            ra.addFlashAttribute("error", "Please select a file");
            return "redirect:/profile";
        }
        try {
            User user = userService.findByEmail(authentication.getName()).orElseThrow();
            String stored = fileUploadService.saveResume(file);
            user.setResumeFileName(stored);
            user.setResumeOriginalName(file.getOriginalFilename());
            userService.updateUser(user);
            ra.addFlashAttribute("sectionMsg", "Resume uploaded successfully!");
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Upload failed: " + e.getMessage());
        }
        return "redirect:/profile/view";
    }
}
