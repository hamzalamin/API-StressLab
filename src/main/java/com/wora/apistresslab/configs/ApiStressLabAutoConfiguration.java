package com.wora.apistresslab.configs;

import com.wora.apistresslab.services.ILoadGeneratorService;
import com.wora.apistresslab.services.LoadGeneratorService;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

@AutoConfiguration
@EnableConfigurationProperties(ApiStressLabProperties.class)
public class ApiStressLabAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public RestTemplate apiStressLabRestTemplate(ApiStressLabProperties properties) {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(properties.getConnectTimeoutMs());
        factory.setReadTimeout(properties.getReadTimeout());
        return new RestTemplate(factory);
    }

    @Bean
    @ConditionalOnMissingBean(ILoadGeneratorService.class)
    public ILoadGeneratorService loadGeneratorService(
            RestTemplate apiStressLabRestTemplate
    ) {
        return new LoadGeneratorService(apiStressLabRestTemplate);
    }
}
