package com.wora.apistresslab.models.DTOs;

import com.wora.apistresslab.models.enums.ExecutionStatus;
import org.springframework.http.HttpMethod;


public record CreateLoadGeneratorDto(
        String url,
        Integer RequestNumber,
        HttpMethod httpMethod,
        ExecutionStatus executionStatus
) {
}
