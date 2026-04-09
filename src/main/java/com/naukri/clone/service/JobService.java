package com.naukri.clone.service;

import com.naukri.clone.model.Job;
import com.naukri.clone.model.JobApplication;
import com.naukri.clone.model.User;
import com.naukri.clone.repository.JobApplicationRepository;
import com.naukri.clone.repository.JobRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class JobService {

    @Autowired
    private JobRepository jobRepository;

    @Autowired
    private JobApplicationRepository applicationRepository;

    // ✅ POST JOB
    public Job postJob(Job job) {
        return jobRepository.save(job);
    }

    // ✅ GET ALL ACTIVE JOBS
    public List<Job> getAllActiveJobs() {
        return jobRepository.findByActiveTrueOrderByPostedAtDesc();
    }

    // ✅ SEARCH JOBS (NULL SAFE)
    public List<Job> searchJobs(String keyword, String location, String category) {
        String kw = (keyword != null && !keyword.trim().isEmpty()) ? keyword.trim() : null;
        String loc = (location != null && !location.trim().isEmpty()) ? location.trim() : null;
        String cat = (category != null && !category.trim().isEmpty()) ? category.trim() : null;

        return jobRepository.searchJobs(kw, loc, cat);
    }

    // ✅ FIND JOB BY ID
    public Optional<Job> findById(Long id) {
        return jobRepository.findById(id);
    }

    // ✅ EMPLOYER JOBS
    public List<Job> getJobsByEmployer(Long employerId) {
        return jobRepository.findByEmployerId(employerId);
    }

    // ✅ APPLY FOR JOB (FULL FIX)
    public JobApplication applyForJob(Job job, User applicant, String coverLetter) {

        // 🔴 DUPLICATE CHECK
        if (applicationRepository.existsByJobIdAndApplicantId(job.getId(), applicant.getId())) {
            throw new RuntimeException("You have already applied for this job!");
        }

        // ✅ CREATE APPLICATION
        JobApplication application = JobApplication.builder()
                .job(job)
                .applicant(applicant)
                .coverLetter(coverLetter)
                .status(JobApplication.ApplicationStatus.APPLIED)
                .build();

        // ✅ SAFE APPLICATION COUNT INCREMENT
        job.setApplicationCount(job.getApplicationCount() + 1);
        jobRepository.save(job);

        return applicationRepository.save(application);
    }

    // ✅ USER APPLICATIONS
    public List<JobApplication> getApplicationsByUser(Long userId) {
        return applicationRepository.findByApplicantId(userId);
    }

    // ✅ JOB APPLICATIONS
    public List<JobApplication> getApplicationsForJob(Long jobId) {
        return applicationRepository.findByJobId(jobId);
    }

    // ✅ UPDATE STATUS
    public JobApplication updateApplicationStatus(Long applicationId, JobApplication.ApplicationStatus status) {
        JobApplication app = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new RuntimeException("Application not found"));

        app.setStatus(status);
        return applicationRepository.save(app);
    }

    // ✅ CHECK ALREADY APPLIED (IMPORTANT FOR UI)
    public boolean hasApplied(Long jobId, Long userId) {
        return applicationRepository.existsByJobIdAndApplicantId(jobId, userId);
    }

    // ✅ DELETE JOB
    public void deleteJob(Long id) {
        jobRepository.deleteById(id);
    }

    // ✅ UPDATE JOB
    public Job updateJob(Job job) {
        return jobRepository.save(job);
    }
}