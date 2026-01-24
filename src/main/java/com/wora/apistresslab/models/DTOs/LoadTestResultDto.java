package com.wora.apistresslab.models.DTOs;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public record LoadTestResultDto(
        Integer totalRequests,
        Integer successfulRequests,
        Long averageResponseTimeMs,
        Long minResponseTimeMs,
        Long maxResponseTimeMs,
        Double requestsPerSecond,
        Map<Integer, Integer> statusCodeDistribution,
        List<String> errors,
        LocalDateTime createdAt
) {
}
