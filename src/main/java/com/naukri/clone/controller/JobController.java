package com.naukri.clone.controller;

import com.naukri.clone.model.Job;
import com.naukri.clone.model.JobApplication;
import com.naukri.clone.model.User;
import com.naukri.clone.service.JobService;
import com.naukri.clone.service.UserService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class JobController {

    @Autowired
    private JobService jobService;

    @Autowired
    private UserService userService;

    // POST JOB FORM
    @GetMapping("/employer/post-job")
    public String postJobForm(Model model) {
        model.addAttribute("job", new Job());
        return "post-job";
    }

    // SAVE JOB
    @PostMapping("/employer/post-job")
    public String postJob(@ModelAttribute Job job, Authentication authentication) {
        User employer = userService.findByEmail(authentication.getName()).orElseThrow();
        job.setEmployer(employer);
        jobService.postJob(job);
        return "redirect:/dashboard?jobPosted=true";
    }

    // APPLY JOB
    @PostMapping("/jobs/{id}/apply")
    public String applyForJob(@PathVariable Long id,
                             @RequestParam(required = false) String coverLetter,
                             Authentication authentication) {

        if (authentication == null) return "redirect:/login";

        Job job = jobService.findById(id).orElseThrow();
        User applicant = userService.findByEmail(authentication.getName()).orElseThrow();

        try {
            jobService.applyForJob(job, applicant, coverLetter);
        } catch (Exception e) {
            return "redirect:/jobs/" + id + "?error=" + e.getMessage();
        }

        return "redirect:/jobs/" + id + "?applySuccess=true";
    }

    // VIEW APPLICATIONS
    @GetMapping("/employer/applications/{jobId}")
    public String viewApplications(@PathVariable Long jobId, Model model) {
        Job job = jobService.findById(jobId).orElseThrow();
        model.addAttribute("job", job);
        model.addAttribute("applications", jobService.getApplicationsForJob(jobId));
        return "applications";
    }

    // UPDATE STATUS
    @PostMapping("/employer/applications/{appId}/status")
    public String updateStatus(@PathVariable Long appId,
                              @RequestParam JobApplication.ApplicationStatus status,
                              @RequestParam Long jobId) {
        jobService.updateApplicationStatus(appId, status);
        return "redirect:/employer/applications/" + jobId;
    }

    // EDIT JOB
    @GetMapping("/employer/edit-job/{id}")
    public String editJobForm(@PathVariable Long id, Model model) {
        Job job = jobService.findById(id).orElseThrow();
        model.addAttribute("job", job);
        return "post-job";
    }

    @PostMapping("/employer/edit-job/{id}")
    public String editJob(@PathVariable Long id, @ModelAttribute Job updatedJob) {
        Job job = jobService.findById(id).orElseThrow();

        job.setTitle(updatedJob.getTitle());
        job.setLocation(updatedJob.getLocation());
        job.setSalary(updatedJob.getSalary());
        job.setExperience(updatedJob.getExperience());
        job.setJobType(updatedJob.getJobType());
        job.setCategory(updatedJob.getCategory());
        job.setDescription(updatedJob.getDescription());
        job.setRequirements(updatedJob.getRequirements());
        job.setSkills(updatedJob.getSkills());

        jobService.updateJob(job);
        return "redirect:/dashboard";
    }

    // DELETE JOB
    @PostMapping("/employer/delete-job/{id}")
    public String deleteJob(@PathVariable Long id) {
        jobService.deleteJob(id);
        return "redirect:/dashboard";
    }
}