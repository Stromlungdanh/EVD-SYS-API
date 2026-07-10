package com.lotte.evdsys.document.dto;

import com.lotte.evdsys.document.DocumentStatus;

import java.time.Instant;

public record DocumentResponse(
        Long id,
        String code,
        String title,
        String description,
        String category,
        DocumentStatus status,
        String createdByUsername,
        Instant createdAt,
        Instant updatedAt,
        String fileName
) {
}
