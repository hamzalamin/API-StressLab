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
    public void printSummary() {
        System.out.println("\n========== Load Test Summary ==========");
        System.out.println("Total Requests: " + totalRequests);
        System.out.println("Successful: " + successfulRequests);
        System.out.println("Failed: " + (totalRequests - successfulRequests));
        System.out.println("Average Response Time: " + averageResponseTimeMs + "ms");
        System.out.println("Min Response Time: " + minResponseTimeMs + "ms");
        System.out.println("Max Response Time: " + maxResponseTimeMs + "ms");
        System.out.println("Requests/Second: " + requestsPerSecond);
        System.out.println("Status Codes: " + statusCodeDistribution);
        System.out.println("========================================\n");
    }
}
