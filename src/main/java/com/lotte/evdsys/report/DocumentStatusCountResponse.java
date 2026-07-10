package com.lotte.evdsys.report;

import com.lotte.evdsys.document.DocumentStatus;

public record DocumentStatusCountResponse(
        DocumentStatus status,
        long total
) {
}
