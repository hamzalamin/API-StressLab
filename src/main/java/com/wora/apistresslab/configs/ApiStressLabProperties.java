/**
 * Configuration properties for API Stress Lab library.
 * Users can customize these in application.properties.
 */


package com.wora.apistresslab.configs;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "api-stress-lab")
public class ApiStressLabProperties {
    private int connectTimeoutMs = 10000;
    private int maxThreads = 1000;
    private int defaultThreads = 10;
    private int defaultRequests = 100;
    private int defaultDurationSeconds = 30;
    private boolean enableDetailedLogging = false;
    private boolean enableRetry = false;
    private int maxRetries = 3;
    private boolean followRedirects = true;
    private int readTimeout = 10000;

}