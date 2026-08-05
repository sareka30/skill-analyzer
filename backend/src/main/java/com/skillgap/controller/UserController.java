package com.skillgap.controller;

import com.skillgap.dto.UserProfileResponse;
import com.skillgap.dto.UserSkillsRequest;
import com.skillgap.model.User;
import com.skillgap.security.UserDetailsImpl;
import com.skillgap.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/student")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    public ResponseEntity<UserProfileResponse> getProfile(@AuthenticationPrincipal UserDetailsImpl principal) {
        User user = userService.findById(principal.getId());
        return ResponseEntity.ok(UserProfileResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .role(user.getRole().name())
                .createdAt(user.getCreatedAt())
                .skills(user.getSkills())
                .build());
    }

    @PutMapping("/skills")
    public ResponseEntity<UserProfileResponse> updateSkills(@AuthenticationPrincipal UserDetailsImpl principal,
                                                              @Valid @RequestBody UserSkillsRequest request) {
        User user = userService.updateSkills(principal.getId(), request.getSkillIds());
        return ResponseEntity.ok(UserProfileResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .role(user.getRole().name())
                .createdAt(user.getCreatedAt())
                .skills(user.getSkills())
                .build());
    }
}
