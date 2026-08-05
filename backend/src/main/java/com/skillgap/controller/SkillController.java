package com.skillgap.controller;

import com.skillgap.dto.SkillRequest;
import com.skillgap.model.Skill;
import com.skillgap.service.SkillService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class SkillController {

    private final SkillService skillService;

    @GetMapping("/api/skills")
    public ResponseEntity<List<Skill>> getAllSkills() {
        return ResponseEntity.ok(skillService.findAll());
    }

    @PostMapping("/api/admin/skills")
    public ResponseEntity<Skill> createSkill(@Valid @RequestBody SkillRequest request) {
        return ResponseEntity.ok(skillService.create(request));
    }

    @PutMapping("/api/admin/skills/{id}")
    public ResponseEntity<Skill> updateSkill(@PathVariable Long id, @Valid @RequestBody SkillRequest request) {
        return ResponseEntity.ok(skillService.update(id, request));
    }

    @DeleteMapping("/api/admin/skills/{id}")
    public ResponseEntity<Void> deleteSkill(@PathVariable Long id) {
        skillService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
