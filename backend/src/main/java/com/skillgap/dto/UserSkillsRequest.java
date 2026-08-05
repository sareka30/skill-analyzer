package com.skillgap.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

@Data
public class UserSkillsRequest {
    @NotEmpty(message = "skillIds must contain at least one skill id")
    private List<Long> skillIds;
}
