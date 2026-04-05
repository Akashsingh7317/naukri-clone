package com.naukri.clone.repository;

import com.naukri.clone.model.JobApplication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface JobApplicationRepository extends JpaRepository<JobApplication, Long> {
    List<JobApplication> findByApplicantId(Long applicantId);
    List<JobApplication> findByJobId(Long jobId);
    Optional<JobApplication> findByJobIdAndApplicantId(Long jobId, Long applicantId);
    boolean existsByJobIdAndApplicantId(Long jobId, Long applicantId);
    long countByJobId(Long jobId);
}
