package com.wora.apistresslab.models.DTOs;

import com.wora.apistresslab.models.enums.ExecutionStatus;
import com.wora.apistresslab.models.enums.HttpMethod;


public record CreateLoadGeneratorDto(
        String url,
        Integer RequestNumber,
        HttpMethod httpMethod,
        ExecutionStatus executionStatus
) {
}
