package com.skillgap.service;

import com.skillgap.dto.ReportResponse;
import com.skillgap.exception.BadRequestException;
import com.skillgap.model.*;
import com.skillgap.repository.JobRoleRepository;
import com.skillgap.repository.SkillGapReportRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Core domain logic: compares a parsed resume against a job role's required
 * skills, computes a match percentage + employability score, and generates
 * targeted learning recommendations for every missing skill.
 */
@Service
@RequiredArgsConstructor
public class SkillGapAnalysisService {

    private final JobRoleRepository jobRoleRepository;
    private final SkillGapReportRepository skillGapReportRepository;
    private final RecommendationCatalogService recommendationCatalogService;
    private final ResumeService resumeService;

    private static final double SKILL_MATCH_WEIGHT = 0.7;
    private static final double PROFILE_COMPLETENESS_WEIGHT = 0.3;

    @Transactional
    public ReportResponse generateReport(User user, Long resumeId, Long jobRoleId) {
        Resume resume = resumeService.findByIdForUser(resumeId, user.getId());
        JobRole jobRole = jobRoleRepository.findById(jobRoleId)
                .orElseThrow(() -> new BadRequestException("Job role not found with id: " + jobRoleId));

        Set<Skill> requiredSkills = jobRole.getRequiredSkills();
        if (requiredSkills.isEmpty()) {
            throw new BadRequestException("Selected job role has no required skills configured yet");
        }

        String resumeTextLower = resume.getExtractedText() == null
                ? "" : resume.getExtractedText().toLowerCase();

        List<Skill> matchedSkills = new ArrayList<>();
        List<Skill> missingSkills = new ArrayList<>();

        for (Skill skill : requiredSkills) {
            boolean foundInResume = resumeTextLower.contains(skill.getName().toLowerCase());
            boolean foundInProfile = user.getSkills() != null && user.getSkills().stream()
                    .anyMatch(s -> s.getId().equals(skill.getId()));

            if (foundInResume || foundInProfile) {
                matchedSkills.add(skill);
            } else {
                missingSkills.add(skill);
            }
        }

        double matchPercentage = (matchedSkills.size() * 100.0) / requiredSkills.size();

        // Employability score blends skill match with overall profile completeness
        // (does the student have a resume with extracted text and a filled-in skill profile).
        double completenessScore = 0.0;
        if (resume.getExtractedText() != null && !resume.getExtractedText().isBlank()) {
            completenessScore += 50.0;
        }
        if (user.getSkills() != null && !user.getSkills().isEmpty()) {
            completenessScore += 50.0;
        }
        double employabilityScore = (matchPercentage * SKILL_MATCH_WEIGHT)
                + (completenessScore * PROFILE_COMPLETENESS_WEIGHT);
        employabilityScore = Math.min(100.0, Math.round(employabilityScore * 100.0) / 100.0);
        matchPercentage = Math.round(matchPercentage * 100.0) / 100.0;

        SkillGapReport report = SkillGapReport.builder()
                .user(user)
                .jobRole(jobRole)
                .resume(resume)
                .matchPercentage(matchPercentage)
                .employabilityScore(employabilityScore)
                .matchedSkills(new HashSet<>(matchedSkills))
                .missingSkills(new HashSet<>(missingSkills))
                .build();

        List<Recommendation> recommendations = missingSkills.stream()
                .map(skill -> {
                    Recommendation rec = recommendationCatalogService.recommendationFor(skill);
                    rec.setReport(report);
                    return rec;
                })
                .collect(Collectors.toList());
        report.setRecommendations(recommendations);

        SkillGapReport saved = skillGapReportRepository.save(report);

        return toResponse(saved);
    }

    @Transactional(readOnly = true)
    public List<ReportResponse> findByUser(Long userId) {
        return skillGapReportRepository.findByUserIdOrderByGeneratedAtDesc(userId).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ReportResponse findByIdForUser(Long reportId, Long userId) {
        SkillGapReport report = skillGapReportRepository.findById(reportId)
                .orElseThrow(() -> new BadRequestException("Report not found with id: " + reportId));
        if (!report.getUser().getId().equals(userId)) {
            throw new BadRequestException("This report does not belong to the current user");
        }
        return toResponse(report);
    }

    private ReportResponse toResponse(SkillGapReport report) {
        return ReportResponse.builder()
                .id(report.getId())
                .jobRoleTitle(report.getJobRole().getTitle())
                .jobRoleDescription(report.getJobRole().getDescription())
                .matchPercentage(report.getMatchPercentage())
                .employabilityScore(report.getEmployabilityScore())
                .generatedAt(report.getGeneratedAt())
                .matchedSkills(new ArrayList<>(report.getMatchedSkills()))
                .missingSkills(new ArrayList<>(report.getMissingSkills()))
                .recommendations(report.getRecommendations())
                .build();
    }
}
