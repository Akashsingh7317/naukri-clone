package com.naukri.clone.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "users")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class User {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank private String fullName;
    @Email @Column(unique = true) private String email;
    @NotBlank private String password;

    @Enumerated(EnumType.STRING)
    private UserRole role;

    private String phone;
    private String location;
    private String qualification;

    @Column(length = 1000) private String skills;

   
    private String experienceYears;
    private String experienceMonths;
    private String experience; 


    private String workStatus; 

 
    private String currentSalary;
    private String expectedSalary;

 
    private String noticePeriod;

   
    private String resumeHeadline;
    @Column(length = 3000) private String profileSummary;

    // Social links
    private String linkedinUrl;
    private String githubUrl;
    private String portfolioUrl;

    // Files
    private String resumeFileName;
    private String resumeOriginalName;
    private String profilePhotoFileName;
    private String profilePhoto;

    private boolean active = true;

    @Column(updatable = false) private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "applicant", cascade = CascadeType.ALL)
    private List<JobApplication> applications;

    @PrePersist protected void onCreate() { createdAt = updatedAt = LocalDateTime.now(); }
    @PreUpdate  protected void onUpdate() { updatedAt = LocalDateTime.now(); }

    public enum UserRole { JOB_SEEKER, EMPLOYER, ADMIN }
}