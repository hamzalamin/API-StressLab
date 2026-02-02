package com.wora.apistresslab.junit;

import com.wora.apistresslab.models.enums.ExecutionStatus;
import com.wora.apistresslab.annotations.LoadTest;
import com.wora.apistresslab.models.DTOs.CreateDurationBasedLoadTestDto;
import com.wora.apistresslab.models.DTOs.CreateLoadGeneratorDto;
import com.wora.apistresslab.models.DTOs.LoadTestResultDto;
import com.wora.apistresslab.services.LoadGeneratorService;
import org.junit.jupiter.api.extension.*;
import org.springframework.http.HttpMethod;

public class LoadTestExtension
        implements BeforeTestExecutionCallback, ParameterResolver {

    private static final ExtensionContext.Namespace NAMESPACE =
            ExtensionContext.Namespace.create(LoadTestExtension.class);

    @Override
    public void beforeTestExecution(ExtensionContext context) {

        LoadTest cfg = context.getRequiredTestMethod()
                .getAnnotation(LoadTest.class);

        LoadGeneratorService service =
                SpringContext.getBean(LoadGeneratorService.class);

        HttpMethod httpMethod =
                HttpMethod.valueOf(cfg.method().name());

        LoadTestResultDto result;

        if (cfg.duration() > 0) {
            result = service.executeDurationBasedLoadTest(
                    new CreateDurationBasedLoadTestDto(
                            cfg.url(),
                            httpMethod,
                            cfg.threads(),
                            cfg.duration(),
                            null
                    )
            );
        } else {
            result = service.executeConcurrentLoadTest(
                    new CreateLoadGeneratorDto(
                            cfg.url(),
                            cfg.requests(),
                            httpMethod,
                            ExecutionStatus.PENDING
                    )
            );
        }

        context.getStore(NAMESPACE).put("result", result);
    }

    @Override
    public boolean supportsParameter(
            ParameterContext pc, ExtensionContext ec) {

        return pc.getParameter()
                .getType()
                .equals(LoadTestResultDto.class);
    }

    @Override
    public Object resolveParameter(
            ParameterContext pc, ExtensionContext ec) {

        return ec.getStore(NAMESPACE)
                .get("result", LoadTestResultDto.class);
    }
}
