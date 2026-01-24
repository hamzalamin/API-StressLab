package com.wora.apistresslab.models.DTOs;

import com.wora.apistresslab.models.enums.ExecutionStatus;
import org.springframework.http.HttpMethod;


public record CreateLoadGeneratorDto(
        String url,
        Integer requestNumber,
        HttpMethod httpMethod,
        ExecutionStatus executionStatus
) {
}
