package com.naukri.clone.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "projects")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class Project {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    private String title;
    private String technologies;
    private String projectUrl;
    private String startYear;
    private String endYear;

    @Column(length = 2000)
    private String description;
}
