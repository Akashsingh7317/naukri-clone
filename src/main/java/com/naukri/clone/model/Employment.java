package com.naukri.clone.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Entity
@Table(name = "employments")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class Employment {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    private String jobTitle;
    private String companyName;
    private String employmentType; // Full-time, Part-time, Internship
    private String location;
    private LocalDate startDate;
    private LocalDate endDate;
    private boolean currentlyWorking;

    @Column(length = 2000)
    private String description;
}
