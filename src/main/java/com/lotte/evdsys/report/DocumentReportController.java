package com.lotte.evdsys.report;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/documents/reports")
@PreAuthorize("hasRole('ADMIN')")
public class DocumentReportController {

    private final DocumentReportService documentReportService;

    public DocumentReportController(DocumentReportService documentReportService) {
        this.documentReportService = documentReportService;
    }

    @GetMapping("/count-by-status")
    public List<DocumentStatusCountResponse> countByStatus() {
        return documentReportService.countByStatus();
    }
}
