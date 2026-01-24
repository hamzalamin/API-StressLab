package com.wora.apistresslab.services;

import com.wora.apistresslab.models.DTOs.CreateLoadGeneratorDto;
import com.wora.apistresslab.models.DTOs.LoadTestResultDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class LoadGeneratorService implements ILoadGeneratorService {

    private final RestTemplate restTemplate;

    @Override
    public LoadTestResultDto executeLoadTest(CreateLoadGeneratorDto createLoadGeneratorDto) {
        String url = createLoadGeneratorDto.url();
        Integer requestNumber = createLoadGeneratorDto.requestNumber();
        HttpMethod httpMethod = createLoadGeneratorDto.httpMethod();

        int successCount = 0;
        int failCount = 0;

        List<Long> responseTimes = new ArrayList<>();
        Map<Integer, Integer> statusCodeDistribution = new HashMap<>();
        List<String> errors = new ArrayList<>();

        Long testStartTime = System.currentTimeMillis();

        for(int i = 0; i < requestNumber; i++) {
            ResponseEntity<String> response = null;
            try {
                Long requestStartTime = System.currentTimeMillis();
                response = makeHttpRequest(url, httpMethod);
                Long requestEndTime = System.currentTimeMillis();

                Long responseTime = requestEndTime - requestStartTime;
                responseTimes.add(responseTime);

                int statusCode = response.getStatusCode().value();
                statusCodeDistribution.put(statusCode, statusCodeDistribution.getOrDefault(statusCode, 0) + 1);

                if (statusCode >= 200 && statusCode < 300) {
                    successCount++;
                } else {
                    failCount++;
                }

            } catch (HttpClientErrorException e) {
                failCount++;
                int statusCode = e.getStatusCode().value();
                statusCodeDistribution.put(statusCode, statusCodeDistribution.getOrDefault(statusCode, 0) + 1);
                errors.add("HTTP " + statusCode + " : " + e.getMessage());

            } catch (ResourceAccessException e) {
                failCount++;
                errors.add("Network Err : " + e.getMessage());

            } catch (Exception e) {
                failCount++;
                errors.add("Unexpected Err : " + e.getMessage());
            }

        }

        Long testEndTime = System.currentTimeMillis();
        Long totalDuration = testEndTime - testStartTime;
        Long averageTimeResponse = calculateAverage(responseTimes);
        Long minResponseTime = responseTimes.isEmpty() ? 0L : Collections.min(responseTimes);
        Long maxResponseTime = responseTimes.isEmpty() ? 0L : Collections.max(responseTimes);
        Double requestDurationPerSec = calculateRequestsPerSec(Long.valueOf(requestNumber), totalDuration);
        List<String> duplicatedErrs = deDuplicateErrs(errors);

        return new LoadTestResultDto(
                requestNumber,
                successCount,
                averageTimeResponse,
                minResponseTime,
                maxResponseTime,
                requestDurationPerSec,
                statusCodeDistribution,
                duplicatedErrs,
                LocalDateTime.now()
        );
    }

    private ResponseEntity<String> makeHttpRequest(String url, HttpMethod httpMethod) {
        HttpHeaders header = new HttpHeaders();
        HttpEntity<String> entity = new HttpEntity<>(null, header);

        return restTemplate.exchange(url, httpMethod, entity, String.class);
    }

    private Long calculateAverage(List<Long> responseTimes) {
        return (long) responseTimes.stream()
                .mapToLong(Long::longValue)
                .average()
                .orElse(0.0);
    }

    private Double calculateRequestsPerSec(Long totalRequests, Long durationMs) {
        if (totalRequests == 0) {
            return 0.0;
        }

        return (totalRequests * 1000.0) / durationMs;
    }

    private List<String> deDuplicateErrs(List<String> errs) {
        Map<String, Integer> errCount = new HashMap<>();

        for (String err : errs) {
            errCount.put(err, errCount.getOrDefault(err, 0) + 1);
        }

        List<String> duplicateErrs = new ArrayList<>();

        for (Map.Entry<String, Integer> entry : errCount.entrySet()) {
            if (entry.getValue() > 1) {
                duplicateErrs.add(entry.getKey() + " (" + entry.getValue() + " occurrences)");
            } else {
                duplicateErrs.add(entry.getKey());
            }
        }

        return duplicateErrs;
    }



}
