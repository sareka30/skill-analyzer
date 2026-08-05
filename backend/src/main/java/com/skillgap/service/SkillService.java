package com.skillgap.service;

import com.skillgap.dto.SkillRequest;
import com.skillgap.exception.BadRequestException;
import com.skillgap.exception.ResourceNotFoundException;
import com.skillgap.model.Skill;
import com.skillgap.repository.SkillRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SkillService {

    private final SkillRepository skillRepository;

    public List<Skill> findAll() {
        return skillRepository.findAll();
    }

    public Skill findById(Long id) {
        return skillRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Skill not found with id: " + id));
    }

    public Skill create(SkillRequest request) {
        skillRepository.findByName(request.getName()).ifPresent(s -> {
            throw new BadRequestException("Skill already exists: " + request.getName());
        });
        Skill skill = Skill.builder()
                .name(request.getName())
                .category(request.getCategory())
                .build();
        return skillRepository.save(skill);
    }

    public Skill update(Long id, SkillRequest request) {
        Skill skill = findById(id);
        skill.setName(request.getName());
        skill.setCategory(request.getCategory());
        return skillRepository.save(skill);
    }

    public void delete(Long id) {
        Skill skill = findById(id);
        skillRepository.delete(skill);
    }
}
