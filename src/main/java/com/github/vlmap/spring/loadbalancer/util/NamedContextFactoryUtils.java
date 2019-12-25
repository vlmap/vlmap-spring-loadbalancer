package com.github.vlmap.spring.loadbalancer.util;

import org.apache.commons.lang3.reflect.FieldUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.context.named.NamedContextFactory;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import java.util.Map;

public class NamedContextFactoryUtils {
    private static Logger logger = LoggerFactory.getLogger(NamedContextFactoryUtils.class);

    public static void close(NamedContextFactory factory, String contextName) {
        try {

            Map<String, AnnotationConfigApplicationContext> contexts = (Map) FieldUtils.readField(factory, "contexts", true);
            for (Map.Entry<String, AnnotationConfigApplicationContext> entry : contexts.entrySet()) {
                if (entry.getKey().equals(contextName)) {
                    AnnotationConfigApplicationContext context = entry.getValue();
                    context.close();
                    contexts.remove(entry.getKey());
                    break;
                }
            }
        } catch (Exception e) {
            logger.error(" close context name:" + contextName + " error", e);

        }


    }
}
