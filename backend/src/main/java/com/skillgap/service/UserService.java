package com.skillgap.service;

import com.skillgap.exception.BadRequestException;
import com.skillgap.exception.ResourceNotFoundException;
import com.skillgap.model.Skill;
import com.skillgap.model.User;
import com.skillgap.repository.SkillRepository;
import com.skillgap.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final SkillRepository skillRepository;

    @Transactional(readOnly = true)
    public User findById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
        user.getSkills().size(); // force-init lazy skills before leaving the transaction
        return user;
    }

    @Transactional
    public User updateSkills(Long userId, List<Long> skillIds) {
        User user = findById(userId);
        Set<Skill> skills = new HashSet<>(skillRepository.findAllById(skillIds));
        if (skills.size() != new HashSet<>(skillIds).size()) {
            throw new BadRequestException("One or more skillIds do not exist");
        }
        user.setSkills(skills);
        return userRepository.save(user);
    }
}
