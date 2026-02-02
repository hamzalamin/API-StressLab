package com.wora.apistresslab.services;

import com.wora.apistresslab.models.DTOs.CreateDurationBasedLoadTestDto;
import com.wora.apistresslab.models.DTOs.CreateLoadGeneratorDto;
import com.wora.apistresslab.models.DTOs.LoadTestResultDto;
import com.wora.apistresslab.models.DTOs.LoadTestStatistics;
import lombok.RequiredArgsConstructor;
import org.apache.catalina.Executor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@RequiredArgsConstructor
public class LoadGeneratorService implements ILoadGeneratorService {

    private final RestTemplate restTemplate;
    private static final Logger LOG = LoggerFactory.getLogger(LoadGeneratorService.class);

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

        for (int i = 0; i < requestNumber; i++) {
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

        LoadTestStatistics stats = computeLoadTestStatistics(testStartTime, responseTimes, requestNumber, errors);
        LOG.info("fail count : {}", failCount);

        return new LoadTestResultDto(
                requestNumber,
                successCount,
                stats.averageResponseTime(),
                stats.minResponseTime(),
                stats.maxResponseTime(),
                stats.requestsPerSecond(),
                statusCodeDistribution,
                stats.deduplicatedErrors(),
                LocalDateTime.now()
        );
    }

    @Override
    public LoadTestResultDto executeConcurrentLoadTest(CreateLoadGeneratorDto createLoadGeneratorDto) {
        String url = createLoadGeneratorDto.url();
        Integer requestNumber = createLoadGeneratorDto.requestNumber();
        HttpMethod httpMethod = createLoadGeneratorDto.httpMethod();

        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failCount = new AtomicInteger(0);

        List<Long> responseTimes = Collections.synchronizedList(new ArrayList<>());
        List<String> errors = Collections.synchronizedList(new ArrayList<>());
        Map<Integer, Integer> statusCodeDistribution = new ConcurrentHashMap<>();

        Long testStartTime = System.currentTimeMillis();
        ExecutorService executorService = Executors.newFixedThreadPool(requestNumber);
        try {

            List<Future<Void>> futures = new ArrayList<>();

            for (int i = 0; i < requestNumber; i++) {
                Future<Void> future = executorService.submit(() -> {
                    executeRequestAndUpdateStatistics(url, httpMethod, responseTimes, statusCodeDistribution, successCount, failCount, errors);
                    return null;
                });

                futures.add(future);
            }

            waitForFuturesCompletion(futures, errors);
        } catch (Exception e) {
            LOG.error("execution exception : {}", e.getMessage());
        } finally {
            shutdownExecutorService(executorService);
        }

        LoadTestStatistics stats = computeLoadTestStatistics(testStartTime, responseTimes, requestNumber, errors);

        return new LoadTestResultDto(
                requestNumber,
                successCount.get(),
                stats.averageResponseTime(),
                stats.minResponseTime(),
                stats.maxResponseTime(),
                stats.requestsPerSecond(),
                statusCodeDistribution,
                stats.deduplicatedErrors(),
                LocalDateTime.now()
        );
    }



    @Override
    public LoadTestResultDto executeDurationBasedLoadTest(CreateDurationBasedLoadTestDto createDurationBasedLoadTestDto) {
        String url = createDurationBasedLoadTestDto.url();
        HttpMethod httpMethod = createDurationBasedLoadTestDto.httpMethod();
        int maxThreads = createDurationBasedLoadTestDto.MaxConcurrentThread();
        int durationSeconds = createDurationBasedLoadTestDto.durationSeconds();

        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failCount = new AtomicInteger(0);

        List<Long> responseTimes = Collections.synchronizedList(new ArrayList<>());
        List<String> errors = Collections.synchronizedList(new ArrayList<>());
        Map<Integer, Integer> statusCodeDistribution = new ConcurrentHashMap<>();

        ExecutorService executorService = Executors.newFixedThreadPool(maxThreads);

        long testStartTime = System.currentTimeMillis();
        long testEndTime = testStartTime * (durationSeconds / 1000L);

        try {
            List<Future<Void>> futures = new ArrayList<>();

            for (int i = 0; i < maxThreads; i++) {
                futures.add(executorService.submit(() -> {
                    while (System.currentTimeMillis() < testEndTime) {
                        executeRequestAndUpdateStatistics(url, httpMethod, responseTimes, statusCodeDistribution, successCount, failCount, errors);
                    }
                    return null;
                }));
            }
            waitForFuturesCompletion(futures, errors);
        } finally {
            shutdownExecutorService(executorService);
        }

        int totalRequests = successCount.get() + failCount.get();
        LoadTestStatistics stats = computeLoadTestStatistics(
                testStartTime,
                responseTimes,
                totalRequests,
                errors
        );

        return new LoadTestResultDto(
                totalRequests,
                successCount.get(),
                stats.averageResponseTime(),
                stats.minResponseTime(),
                stats.maxResponseTime(),
                stats.requestsPerSecond(),
                statusCodeDistribution,
                stats.deduplicatedErrors(),
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

    private void executeRequestAndUpdateStatistics(
            String url, HttpMethod httpMethod, List<Long> responseTimes, Map<Integer, Integer> statusCodeDistribution, AtomicInteger successCount,
            AtomicInteger failCount, List<String> errors
    ){
        try {
            Long startRequestTime = System.currentTimeMillis();
            ResponseEntity<String> response = makeHttpRequest(url, httpMethod);
            Long endRequestTime = System.currentTimeMillis();

            Long responseTime = endRequestTime - startRequestTime;
            responseTimes.add(responseTime);

            int statusCode = response.getStatusCode().value();
            statusCodeDistribution.merge(statusCode, 1, Integer::sum);

            if (statusCode >= 200 && statusCode < 300) {
                successCount.incrementAndGet();
            } else {
                failCount.incrementAndGet();
            }

        } catch (HttpClientErrorException e) {
            failCount.incrementAndGet();
            int statusCode = e.getStatusCode().value();
            statusCodeDistribution.merge(statusCode, 1, Integer::sum);
            errors.add("HTTP " + statusCode + " : " + e.getMessage());
        } catch (ResourceAccessException e) {
            failCount.incrementAndGet();
            errors.add("Network err : " + e.getMessage());
        } catch (Exception e) {
            failCount.incrementAndGet();
            errors.add("Unexpected exception : " + e.getMessage());
        }
    }

    private LoadTestStatistics computeLoadTestStatistics(
            Long testStartTime,
            List<Long> responseTimes,
            Integer requestNumber,
            List<String> errors
    ) {
        Long testEndTime = System.currentTimeMillis();
        Long totalDuration = testEndTime - testStartTime;

        Long averageResponseTime = calculateAverage(responseTimes);
        Long minResponseTime = responseTimes.isEmpty() ? 0L : Collections.min(responseTimes);
        Long maxResponseTime = responseTimes.isEmpty() ? 0L : Collections.max(responseTimes);
        Double requestsPerSecond = calculateRequestsPerSec(requestNumber.longValue(), totalDuration);

        List<String> deduplicatedErrors = deDuplicateErrs(errors);

        return new LoadTestStatistics(
                averageResponseTime,
                minResponseTime,
                maxResponseTime,
                requestsPerSecond,
                deduplicatedErrors
        );
    }

    private void waitForFuturesCompletion(List<Future<Void>> futures, List<String> errors) {
        for (Future<Void> future : futures) {
            try {
                future.get();
            } catch (InterruptedException e) {
                LOG.error("Thread interrupted while waiting for task completion", e);
                errors.add("Thread interrupted : " + e.getMessage());
                Thread.currentThread().interrupt();
                return;
            } catch (ExecutionException e) {
                LOG.error("Task execution failed", e);
                errors.add("Thread execution err : " + e.getCause().getMessage());
            }
        }
    }

    private void shutdownExecutorService(ExecutorService executorService) {
        executorService.shutdown();
        try {
            if (!executorService.awaitTermination(60, TimeUnit.SECONDS)) {
                executorService.shutdownNow();
            }
        } catch (InterruptedException e) {
            executorService.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }

}
