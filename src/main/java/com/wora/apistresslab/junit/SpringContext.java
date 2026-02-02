/**
 * Provides static access to Spring beans for non-Spring components (JUnit extensions).
 * Spring automatically calls setApplicationContext() on startup to store the context.
 * Other classes can then call getBean() to retrieve Spring-managed beans.
 */

package com.wora.apistresslab.junit;

import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

@Component
public class SpringContext implements ApplicationContextAware {

    private static volatile ApplicationContext context;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        SpringContext.context = applicationContext;
    }

    public static <T> T getBean(Class<T> type) {
        if (context == null) {
            throw new IllegalStateException(
                    "Spring ApplicationContext not initialized. " +
                            "Did you forget @SpringBootTest?"
            );
        }
        return context.getBean(type);
    }
}