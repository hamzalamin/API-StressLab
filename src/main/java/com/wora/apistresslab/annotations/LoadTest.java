package com.wora.apistresslab.annotations;

import com.wora.apistresslab.junit.LoadTestExtension;
import com.wora.apistresslab.models.enums.LoadHttpMethod;
import org.junit.jupiter.api.extension.ExtendWith;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@ExtendWith(LoadTestExtension.class)
public @interface LoadTest {
    String url();
    LoadHttpMethod method();
    int threads() default 1;
    int requests() default -1;
    int duration() default -1;
}
