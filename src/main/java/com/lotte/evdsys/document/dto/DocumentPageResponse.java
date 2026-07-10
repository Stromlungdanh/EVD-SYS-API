package com.lotte.evdsys.document.dto;

import java.util.List;

public record DocumentPageResponse(
        List<DocumentResponse> content,
        int page,
        int size,
        long totalElements,
        int totalPages,
        String sort
) {
}
