package com.lotte.evdsys.report;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DocumentReportService {

    private final DocumentReportJdbcRepository documentReportJdbcRepository;

    public DocumentReportService(DocumentReportJdbcRepository documentReportJdbcRepository) {
        this.documentReportJdbcRepository = documentReportJdbcRepository;
    }

    public List<DocumentStatusCountResponse> countByStatus() {
        return documentReportJdbcRepository.countByStatus();
    }
}
