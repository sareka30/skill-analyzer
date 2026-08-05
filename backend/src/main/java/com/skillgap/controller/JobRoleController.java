package com.skillgap.controller;

import com.skillgap.dto.RoleRequest;
import com.skillgap.model.JobRole;
import com.skillgap.service.JobRoleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class JobRoleController {

    private final JobRoleService jobRoleService;

    @GetMapping("/api/roles")
    public ResponseEntity<List<JobRole>> getAllRoles() {
        return ResponseEntity.ok(jobRoleService.findAll());
    }

    @GetMapping("/api/roles/{id}")
    public ResponseEntity<JobRole> getRole(@PathVariable Long id) {
        return ResponseEntity.ok(jobRoleService.findById(id));
    }

    @PostMapping("/api/admin/roles")
    public ResponseEntity<JobRole> createRole(@Valid @RequestBody RoleRequest request) {
        return ResponseEntity.ok(jobRoleService.create(request));
    }

    @PutMapping("/api/admin/roles/{id}")
    public ResponseEntity<JobRole> updateRole(@PathVariable Long id, @Valid @RequestBody RoleRequest request) {
        return ResponseEntity.ok(jobRoleService.update(id, request));
    }

    @DeleteMapping("/api/admin/roles/{id}")
    public ResponseEntity<Void> deleteRole(@PathVariable Long id) {
        jobRoleService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
