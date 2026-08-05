package com.skillgap.service;

import com.skillgap.exception.BadRequestException;
import com.skillgap.exception.ResourceNotFoundException;
import com.skillgap.model.Resume;
import com.skillgap.model.User;
import com.skillgap.repository.ResumeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ResumeService {

    private final ResumeRepository resumeRepository;
    private final FileStorageService fileStorageService;
    private final ResumeParserService resumeParserService;

    public Resume upload(User user, MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BadRequestException("Please select a resume file to upload");
        }

        String extractedText = resumeParserService.extractText(file);
        String storedPath = fileStorageService.store(file);

        Resume resume = Resume.builder()
                .user(user)
                .fileName(StringUtils.cleanPath(file.getOriginalFilename()))
                .filePath(storedPath)
                .extractedText(extractedText)
                .build();

        return resumeRepository.save(resume);
    }

    public List<Resume> findByUser(Long userId) {
        return resumeRepository.findByUserIdOrderByUploadedAtDesc(userId);
    }

    public Resume findByIdForUser(Long resumeId, Long userId) {
        Resume resume = resumeRepository.findById(resumeId)
                .orElseThrow(() -> new ResourceNotFoundException("Resume not found with id: " + resumeId));
        if (!resume.getUser().getId().equals(userId)) {
            throw new BadRequestException("This resume does not belong to the current user");
        }
        return resume;
    }
}
