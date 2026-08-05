package com.skillgap.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ResumeResponse {
    private Long id;
    private String fileName;
    private LocalDateTime uploadedAt;
    private Integer extractedTextLength;
}
