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

    public Job postJob(Job job) {
        return jobRepository.save(job);
    }

    public List<Job> getAllActiveJobs() {
        return jobRepository.findByActiveTrueOrderByPostedAtDesc();
    }

    public List<Job> searchJobs(String keyword, String location, String category) {
        String kw = (keyword != null && !keyword.trim().isEmpty()) ? keyword.trim() : null;
        String loc = (location != null && !location.trim().isEmpty()) ? location.trim() : null;
        String cat = (category != null && !category.trim().isEmpty()) ? category.trim() : null;
        return jobRepository.searchJobs(kw, loc, cat);
    }

    public Optional<Job> findById(Long id) {
        return jobRepository.findById(id);
    }

    public List<Job> getJobsByEmployer(Long employerId) {
        return jobRepository.findByEmployerId(employerId);
    }

    public JobApplication applyForJob(Job job, User applicant, String coverLetter) {
        if (applicationRepository.existsByJobIdAndApplicantId(job.getId(), applicant.getId())) {
            throw new RuntimeException("You have already applied for this job!");
        }
        JobApplication application = JobApplication.builder()
                .job(job)
                .applicant(applicant)
                .coverLetter(coverLetter)
                .status(JobApplication.ApplicationStatus.APPLIED)
                .build();
        job.setApplicationCount(job.getApplicationCount() + 1);
        jobRepository.save(job);
        return applicationRepository.save(application);
    }

    public List<JobApplication> getApplicationsByUser(Long userId) {
        return applicationRepository.findByApplicantId(userId);
    }

    public List<JobApplication> getApplicationsForJob(Long jobId) {
        return applicationRepository.findByJobId(jobId);
    }

    public JobApplication updateApplicationStatus(Long applicationId, JobApplication.ApplicationStatus status) {
        JobApplication app = applicationRepository.findById(applicationId)
                .orElseThrow(() -> new RuntimeException("Application not found"));
        app.setStatus(status);
        return applicationRepository.save(app);
    }

    public boolean hasApplied(Long jobId, Long userId) {
        return applicationRepository.existsByJobIdAndApplicantId(jobId, userId);
    }

    public void deleteJob(Long id) {
        jobRepository.deleteById(id);
    }

    public Job updateJob(Job job) {
        return jobRepository.save(job);
    }
}
