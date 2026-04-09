package com.naukri.clone.controller;

import com.naukri.clone.model.*;
import com.naukri.clone.repository.*;
import com.naukri.clone.service.FileUploadService;
import com.naukri.clone.service.JobService;
import com.naukri.clone.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.nio.file.Path;
import java.util.List;

@Controller
public class HomeController {

    @Autowired private JobService jobService;
    @Autowired private UserService userService;
    @Autowired private FileUploadService fileUploadService;
    @Autowired private EmploymentRepository employmentRepo;
    @Autowired private EducationRepository educationRepo;
    @Autowired private ProjectRepository projectRepo;
    @Autowired private ItSkillRepository itSkillRepo;

    @GetMapping("/")
    public String home(Model model) {
        List<Job> featuredJobs = jobService.getAllActiveJobs();
        model.addAttribute("featuredJobs", featuredJobs.stream().limit(8).toList());
        model.addAttribute("totalJobs", featuredJobs.size());
        return "index";
    }

    @GetMapping("/jobs")
    public String jobs(Model model,
                       @RequestParam(required = false) String keyword,
                       @RequestParam(required = false) String location,
                       @RequestParam(required = false) String category) {
        var jobs = ((keyword != null && !keyword.isEmpty()) || (location != null && !location.isEmpty()) || (category != null && !category.isEmpty()))
                ? jobService.searchJobs(keyword, location, category)
                : jobService.getAllActiveJobs();
        model.addAttribute("jobs", jobs);
        model.addAttribute("keyword", keyword);
        model.addAttribute("location", location);
        model.addAttribute("category", category);
        model.addAttribute("totalResults", jobs.size());
        return "jobs";
    }

    @GetMapping("/jobs/{id}")
    public String jobDetail(@PathVariable Long id, Model model, Authentication auth) {
        Job job = jobService.findById(id).orElseThrow();
        model.addAttribute("job", job);
        if (auth != null) {
            User user = userService.findByEmail(auth.getName()).orElse(null);
            if (user != null) {
                model.addAttribute("hasApplied", jobService.hasApplied(id, user.getId()));
                model.addAttribute("currentUser", user);
            }
        }
        return "job-detail";
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model, Authentication auth) {
        if (auth == null) return "redirect:/login";
        User user = userService.findByEmail(auth.getName()).orElseThrow();
        model.addAttribute("user", user);
        if (user.getRole() == User.UserRole.JOB_SEEKER) {
            model.addAttribute("applications", jobService.getApplicationsByUser(user.getId()));
            return "dashboard-seeker";
        } else {
            model.addAttribute("postedJobs", jobService.getJobsByEmployer(user.getId()));
            return "dashboard-employer";
        }
    }

    @GetMapping("/register")
    public String registerForm(Model model) { model.addAttribute("user", new User()); return "register"; }

    @PostMapping("/register")
    public String registerUser(@ModelAttribute User user, RedirectAttributes ra) {
        try { userService.registerUser(user); return "redirect:/login?registered=true"; }
        catch (Exception e) { ra.addFlashAttribute("error", e.getMessage()); return "redirect:/register?error=true"; }
    }

    @GetMapping("/login")
    public String loginForm() { return "login"; }

    // ── PROFILE VIEW (Naukri-style) ──
    @GetMapping("/profile/view")
    public String profileView(Model model, Authentication auth) {
        if (auth == null) return "redirect:/login";
        User user = userService.findByEmail(auth.getName()).orElseThrow();
        model.addAttribute("user", user);
        model.addAttribute("employments", employmentRepo.findByUserIdOrderByStartDateDesc(user.getId()));
        model.addAttribute("educations",  educationRepo.findByUserIdOrderByEndYearDesc(user.getId()));
        model.addAttribute("projects",    projectRepo.findByUserId(user.getId()));
        model.addAttribute("itSkills",    itSkillRepo.findByUserId(user.getId()));
        return "profile-view";
    }

    // ── PROFILE EDIT (simple) ──
    @GetMapping("/profile")
    public String profile(Model model, Authentication auth) {
        if (auth == null) return "redirect:/login";
        model.addAttribute("user", userService.findByEmail(auth.getName()).orElseThrow());
        return "profile-view"; // redirect to full view page
    }

    //handles all fields
    @PostMapping("/profile/update")
    public String updateProfile(
            @RequestParam(required = false) String fullName,
            @RequestParam(required = false) String phone,
            @RequestParam(required = false) String location,
            @RequestParam(required = false) String qualification,
            @RequestParam(required = false) String skills,
            @RequestParam(required = false) String experienceYears,
            @RequestParam(required = false) String experienceMonths,
            @RequestParam(required = false) String workStatus,
            @RequestParam(required = false) String currentSalary,
            @RequestParam(required = false) String expectedSalary,
            @RequestParam(required = false) String noticePeriod,
            @RequestParam(required = false) String resumeHeadline,
            @RequestParam(required = false) String profileSummary,
            @RequestParam(required = false) String linkedinUrl,
            @RequestParam(required = false) String githubUrl,
            @RequestParam(required = false) String portfolioUrl,
            @RequestParam(value = "resumeFile", required = false) MultipartFile resumeFile,
            @RequestParam(value = "photoFile",  required = false) MultipartFile photoFile,
            Authentication auth, RedirectAttributes ra) {

        User user = userService.findByEmail(auth.getName()).orElseThrow();

        if (fullName   != null && !fullName.isBlank())   user.setFullName(fullName);
        if (phone      != null) user.setPhone(phone);
        if (location   != null) user.setLocation(location);
        if (qualification != null) user.setQualification(qualification);
        if (skills     != null) user.setSkills(skills);
        if (workStatus != null) user.setWorkStatus(workStatus);
        if (currentSalary  != null) user.setCurrentSalary(currentSalary);
        if (expectedSalary != null) user.setExpectedSalary(expectedSalary);
        if (noticePeriod   != null) user.setNoticePeriod(noticePeriod);
        if (resumeHeadline != null) user.setResumeHeadline(resumeHeadline);
        if (profileSummary != null) user.setProfileSummary(profileSummary);
        if (linkedinUrl != null) user.setLinkedinUrl(linkedinUrl);
        if (githubUrl   != null) user.setGithubUrl(githubUrl);
        if (portfolioUrl != null) user.setPortfolioUrl(portfolioUrl);

        // Build experience  to display string.
        if (experienceYears != null || experienceMonths != null) {
            String yrs = (experienceYears  != null && !experienceYears.isBlank())  ? experienceYears  : "0";
            String mts = (experienceMonths != null && !experienceMonths.isBlank()) ? experienceMonths : "0";
            user.setExperienceYears(yrs);
            user.setExperienceMonths(mts);
            String expStr = "";
            if (!yrs.equals("0")) expStr += yrs + " Year" + (yrs.equals("1") ? "" : "s");
            if (!mts.equals("0")) expStr += (!expStr.isEmpty() ? " " : "") + mts + " Month" + (mts.equals("1") ? "" : "s");
            user.setExperience(expStr.isEmpty() ? "Fresher" : expStr);
        }

        if (resumeFile != null && !resumeFile.isEmpty()) {
            try {
                user.setResumeFileName(fileUploadService.saveResume(resumeFile));
                user.setResumeOriginalName(resumeFile.getOriginalFilename());
            } catch (Exception e) { ra.addFlashAttribute("sectionMsg", "Resume upload failed: " + e.getMessage()); return "redirect:/profile/view"; }
        }
        if (photoFile != null && !photoFile.isEmpty()) {
            try { user.setProfilePhotoFileName(fileUploadService.saveProfilePhoto(photoFile)); }
            catch (Exception e) { ra.addFlashAttribute("sectionMsg", "Photo upload failed: " + e.getMessage()); return "redirect:/profile/view"; }
        }

        userService.updateUser(user);
        ra.addFlashAttribute("sectionMsg", "Profile updated successfully!");
        return "redirect:/profile/view";
    }

    //DELETE PHOTO.
    @PostMapping("/profile/photo/delete")
    public String deletePhoto(Authentication auth, RedirectAttributes ra) {
        User user = userService.findByEmail(auth.getName()).orElseThrow();
        user.setProfilePhotoFileName(null);
        userService.updateUser(user);
        ra.addFlashAttribute("sectionMsg", "Profile photo deleted.");
        return "redirect:/profile/view";
    }

    //DELETE RESUME..
    @PostMapping("/profile/resume/delete")
    public String deleteResume(Authentication auth, RedirectAttributes ra) {
        User user = userService.findByEmail(auth.getName()).orElseThrow();
        user.setResumeFileName(null);
        user.setResumeOriginalName(null);
        userService.updateUser(user);
        ra.addFlashAttribute("sectionMsg", "Resume deleted.");
        return "redirect:/profile/view";
    }

    //FILE SERVE.
    @GetMapping("/resume/download/{fileName}")
    public ResponseEntity<Resource> downloadResume(@PathVariable String fileName) {
        try {
            Path p = fileUploadService.getResumePath(fileName);
            Resource r = new UrlResource(p.toUri());
            if (r.exists()) return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"").body(r);
        } catch (Exception ignored) {}
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/photo/{fileName}")
    public ResponseEntity<Resource> servePhoto(@PathVariable String fileName) {
        try {
            Path p = fileUploadService.getPhotoPath(fileName);
            Resource r = new UrlResource(p.toUri());
            if (r.exists()) return ResponseEntity.ok().contentType(MediaType.parseMediaType(fileName.endsWith(".png") ? "image/png" : "image/jpeg")).body(r);
        } catch (Exception ignored) {}
        return ResponseEntity.notFound().build();
    }
}