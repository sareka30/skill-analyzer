package com.skillgap.controller;

import com.skillgap.dto.ReportResponse;
import com.skillgap.security.UserDetailsImpl;
import com.skillgap.service.SkillGapAnalysisService;
import com.skillgap.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/student/reports")
@RequiredArgsConstructor
public class ReportController {

    private final SkillGapAnalysisService skillGapAnalysisService;
    private final UserService userService;

    @PostMapping("/generate")
    public ResponseEntity<ReportResponse> generate(@AuthenticationPrincipal UserDetailsImpl principal,
                                                     @RequestParam Long resumeId,
                                                     @RequestParam Long jobRoleId) {
        var user = userService.findById(principal.getId());
        return ResponseEntity.ok(skillGapAnalysisService.generateReport(user, resumeId, jobRoleId));
    }

    @GetMapping
    public ResponseEntity<List<ReportResponse>> myReports(@AuthenticationPrincipal UserDetailsImpl principal) {
        return ResponseEntity.ok(skillGapAnalysisService.findByUser(principal.getId()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReportResponse> getReport(@AuthenticationPrincipal UserDetailsImpl principal,
                                                      @PathVariable Long id) {
        return ResponseEntity.ok(skillGapAnalysisService.findByIdForUser(id, principal.getId()));
    }
}
