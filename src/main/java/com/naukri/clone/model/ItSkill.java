package com.naukri.clone.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "it_skills")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class ItSkill {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    private String skillName;
    private String version;     // Latest, etc.
    private String lastUsed;    // 2025
    private String experience;  // 1 Year 2 Months
}
