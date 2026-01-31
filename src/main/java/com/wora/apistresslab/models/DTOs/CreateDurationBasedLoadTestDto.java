package com.wora.apistresslab.models.DTOs;

import com.wora.apistresslab.models.enums.ExecutionStatus;
import org.springframework.http.HttpMethod;


public record CreateDurationBasedLoadTestDto(
        String url,
        HttpMethod httpMethod,
        Integer MaxConcurrentThread,
        Integer durationSeconds,
        ExecutionStatus executionStatus
) {
}
