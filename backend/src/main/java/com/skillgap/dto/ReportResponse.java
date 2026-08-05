package com.skillgap.dto;

import com.skillgap.model.Recommendation;
import com.skillgap.model.Skill;
import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class ReportResponse {
    private Long id;
    private String jobRoleTitle;
    private String jobRoleDescription;
    private Double matchPercentage;
    private Double employabilityScore;
    private LocalDateTime generatedAt;
    private List<Skill> matchedSkills;
    private List<Skill> missingSkills;
    private List<Recommendation> recommendations;
}
