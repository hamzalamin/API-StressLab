package com.wora.apistresslab.models.DTOs;

import java.util.List;

public record LoadTestStatistics(
        Long averageResponseTime,
        Long minResponseTime,
        Long maxResponseTime,
        Double requestsPerSecond,
        List<String> deduplicatedErrors
) {}

