package com.wora.apistresslab.junit;

import com.wora.apistresslab.models.enums.ExecutionStatus;
import com.wora.apistresslab.annotations.LoadTest;
import com.wora.apistresslab.models.DTOs.CreateDurationBasedLoadTestDto;
import com.wora.apistresslab.models.DTOs.CreateLoadGeneratorDto;
import com.wora.apistresslab.models.DTOs.LoadTestResultDto;
import com.wora.apistresslab.services.ILoadGeneratorService;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolver;
import org.springframework.http.HttpMethod;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.junit.jupiter.SpringExtension;


public class LoadTestExtension
        implements BeforeEachCallback, ParameterResolver {

    private static final ExtensionContext.Namespace NAMESPACE =
            ExtensionContext.Namespace.create(LoadTestExtension.class);

    @Override
    public void beforeEach(ExtensionContext context) {

        ApplicationContext springContext =
                SpringExtension.getApplicationContext(context);

        LoadTest cfg = context.getRequiredTestMethod()
                .getAnnotation(LoadTest.class);

        ILoadGeneratorService service =
                springContext.getBean(ILoadGeneratorService.class);

        LoadTestResultDto result;

        if (cfg.duration() > 0) {
            result = service.executeDurationBasedLoadTest(
                    new CreateDurationBasedLoadTestDto(
                            cfg.url(),
                            HttpMethod.valueOf(cfg.method().name()),
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
                            HttpMethod.valueOf(cfg.method().name()),
                            ExecutionStatus.PENDING
                    )
            );
        }

        context.getStore(NAMESPACE).put("result", result);
    }

    @Override
    public boolean supportsParameter(
            ParameterContext pc, ExtensionContext ec) {

        return pc.getParameter().getType()
                .equals(LoadTestResultDto.class);
    }

    @Override
    public Object resolveParameter(
            ParameterContext pc, ExtensionContext ec) {

        return ec.getStore(NAMESPACE)
                .get("result", LoadTestResultDto.class);
    }
}
