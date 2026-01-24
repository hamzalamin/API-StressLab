package com.wora.apistresslab.models.DTOs;

import com.wora.apistresslab.models.enums.ExecutionStatus;
import org.springframework.http.HttpMethod;

import java.time.LocalDateTime;

public record LoadGeneratorDto(
        Long id,
        String url,
        Integer RequestNumber,
        HttpMethod httpMethod,
        ExecutionStatus executionStatus,
        LocalDateTime createdAt,
        LocalDateTime executedAt
) {
}
