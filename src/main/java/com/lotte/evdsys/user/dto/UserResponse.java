package com.lotte.evdsys.user.dto;

import com.lotte.evdsys.user.Role;

import java.time.Instant;

public record UserResponse(
        Long id,
        String username,
        Role role,
        Instant createdAt
) {
}
