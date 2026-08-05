package com.skillgap.service;

import com.skillgap.dto.RoleRequest;
import com.skillgap.exception.BadRequestException;
import com.skillgap.exception.ResourceNotFoundException;
import com.skillgap.model.JobRole;
import com.skillgap.model.Skill;
import com.skillgap.repository.JobRoleRepository;
import com.skillgap.repository.SkillRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class JobRoleService {

    private final JobRoleRepository jobRoleRepository;
    private final SkillRepository skillRepository;

    @Transactional(readOnly = true)
    public List<JobRole> findAll() {
        List<JobRole> roles = jobRoleRepository.findAll();
        roles.forEach(r -> r.getRequiredSkills().size()); // force-init lazy skills before leaving the transaction
        return roles;
    }

    @Transactional(readOnly = true)
    public JobRole findById(Long id) {
        JobRole jobRole = jobRoleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Job role not found with id: " + id));
        jobRole.getRequiredSkills().size();
        return jobRole;
    }

    private Set<Skill> resolveSkills(List<Long> skillIds) {
        if (skillIds == null || skillIds.isEmpty()) {
            return new HashSet<>();
        }
        Set<Skill> skills = new HashSet<>(skillRepository.findAllById(skillIds));
        if (skills.size() != new HashSet<>(skillIds).size()) {
            throw new BadRequestException("One or more skillIds do not exist");
        }
        return skills;
    }

    public JobRole create(RoleRequest request) {
        jobRoleRepository.findByTitle(request.getTitle()).ifPresent(r -> {
            throw new BadRequestException("Job role already exists: " + request.getTitle());
        });
        JobRole jobRole = JobRole.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .requiredSkills(resolveSkills(request.getSkillIds()))
                .build();
        return jobRoleRepository.save(jobRole);
    }

    public JobRole update(Long id, RoleRequest request) {
        JobRole jobRole = findById(id);
        jobRole.setTitle(request.getTitle());
        jobRole.setDescription(request.getDescription());
        jobRole.setRequiredSkills(resolveSkills(request.getSkillIds()));
        return jobRoleRepository.save(jobRole);
    }

    public void delete(Long id) {
        JobRole jobRole = findById(id);
        jobRoleRepository.delete(jobRole);
    }
}
