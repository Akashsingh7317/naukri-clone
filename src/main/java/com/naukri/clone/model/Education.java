package com.naukri.clone.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "educations")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class Education {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    private String degree;       // MBA, B.Tech, Class XII, etc.
    private String fieldOfStudy;
    private String institution;
    private String startYear;
    private String endYear;
    private String studyType;    
    private String grade;
}
