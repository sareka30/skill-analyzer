package com.skillgap.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "recommendations")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Recommendation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "report_id", nullable = false)
    @JsonIgnore
    private SkillGapReport report;

    @Column(nullable = false, length = 50)
    private String type; // COURSE, CERTIFICATION, PROJECT, CODING_TOPIC

    @Column(nullable = false, length = 150)
    private String title;

    @Column(name = "provider_or_platform", nullable = false, length = 100)
    private String providerOrPlatform;

    @Column(length = 255)
    private String url;

    @Column(columnDefinition = "TEXT")
    private String description;
}
