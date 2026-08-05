package com.skillgap.service;

import com.skillgap.exception.BadRequestException;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;

/**
 * Extracts plain text from an uploaded resume so it can be scanned for
 * skill keywords. Supports PDF (Apache PDFBox) and DOCX (Apache POI).
 */
@Service
public class ResumeParserService {

    public String extractText(MultipartFile file) {
        String originalFilename = file.getOriginalFilename() == null ? "" : file.getOriginalFilename().toLowerCase();

        try (InputStream inputStream = file.getInputStream()) {
            if (originalFilename.endsWith(".pdf")) {
                try (PDDocument document = PDDocument.load(inputStream)) {
                    return new PDFTextStripper().getText(document);
                }
            } else if (originalFilename.endsWith(".docx")) {
                try (XWPFDocument document = new XWPFDocument(inputStream);
                     XWPFWordExtractor extractor = new XWPFWordExtractor(document)) {
                    return extractor.getText();
                }
            } else {
                throw new BadRequestException("Unsupported file type. Only PDF and DOCX are supported.");
            }
        } catch (IOException e) {
            throw new BadRequestException("Failed to read resume file: " + e.getMessage());
        }
    }
}
