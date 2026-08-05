package com.skillgap.controller;

import com.skillgap.dto.ResumeResponse;
import com.skillgap.model.Resume;
import com.skillgap.model.User;
import com.skillgap.security.UserDetailsImpl;
import com.skillgap.service.ResumeService;
import com.skillgap.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/student/resumes")
@RequiredArgsConstructor
public class ResumeController {

    private final ResumeService resumeService;
    private final UserService userService;

    @PostMapping(value = "/upload", consumes = "multipart/form-data")
    public ResponseEntity<ResumeResponse> upload(@AuthenticationPrincipal UserDetailsImpl principal,
                                                   @RequestParam("file") MultipartFile file) {
        User user = userService.findById(principal.getId());
        Resume resume = resumeService.upload(user, file);
        return ResponseEntity.ok(toResponse(resume));
    }

    @GetMapping
    public ResponseEntity<List<ResumeResponse>> myResumes(@AuthenticationPrincipal UserDetailsImpl principal) {
        List<ResumeResponse> resumes = resumeService.findByUser(principal.getId()).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(resumes);
    }

    private ResumeResponse toResponse(Resume resume) {
        return ResumeResponse.builder()
                .id(resume.getId())
                .fileName(resume.getFileName())
                .uploadedAt(resume.getUploadedAt())
                .extractedTextLength(resume.getExtractedText() == null ? 0 : resume.getExtractedText().length())
                .build();
    }
}
