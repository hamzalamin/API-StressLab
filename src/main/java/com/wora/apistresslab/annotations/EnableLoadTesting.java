package com.wora.apistresslab.annotations;

import com.wora.apistresslab.configs.ApiStressLabAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@SpringBootTest
@Import(ApiStressLabAutoConfiguration.class)
public @interface EnableLoadTesting {
}