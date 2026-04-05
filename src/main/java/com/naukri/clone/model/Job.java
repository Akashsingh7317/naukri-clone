package com.naukri.clone.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "jobs")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Job {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String title;

    @NotBlank
    private String company;

    @NotBlank
    private String location;

    private String salary;
    private String experience;
    private String jobType;
    private String category;

    @Column(length = 5000)
    private String description;

    @Column(length = 3000)
    private String requirements;

    @Column(length = 2000)
    private String skills;

    private String companyLogo;
    private boolean active = true;
    private int applicationCount = 0;

    @Column(updatable = false)
    private LocalDateTime postedAt;
    private LocalDateTime updatedAt;

    @ManyToOne
    @JoinColumn(name = "employer_id")
    private User employer;

    @OneToMany(mappedBy = "job", cascade = CascadeType.ALL)
    private List<JobApplication> applications;

    @PrePersist
    protected void onCreate() {
        postedAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
