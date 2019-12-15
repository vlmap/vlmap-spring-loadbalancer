package com.github.vlmap.spring.tools.actuator;

import com.github.vlmap.spring.tools.SpringToolsProperties;
import com.github.vlmap.spring.tools.common.PropertiesUtils;
import com.netflix.config.ConfigurationManager;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.actuate.endpoint.annotation.*;
import org.springframework.cloud.context.environment.EnvironmentChangeEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.core.env.Environment;
import org.springframework.core.env.MapPropertySource;

import javax.annotation.PostConstruct;
import java.util.*;

@Endpoint(
        id = "props"
)
public class PropertiesEndPoint implements ApplicationEventPublisherAware {
    Logger logger = LoggerFactory.getLogger(this.getClass());
    private SpringToolsProperties properties;
    protected ApplicationEventPublisher publisher;
    private Map source = Collections.emptyMap();
    private Environment environment;

    public PropertiesEndPoint(Environment environment, SpringToolsProperties properties) {
        this.environment = environment;
        this.properties = properties;
    }

    @PostConstruct
    public void initMethod() {
        MapPropertySource propertySource = PropertiesUtils.getPropertiesSource(environment, properties);
        if (propertySource != null) {
            this.source = propertySource.getSource();
        }
    }

    @ReadOperation
    public Response props() {

        return new Response(new TreeMap(this.source));
    }

    @ReadOperation
    public Response get(@Selector String name) {
        Configuration configuration = ConfigurationManager.getConfigInstance().subset(name);
        Map<String, String> map = new TreeMap();
        Iterator<String> iterator = configuration.getKeys();
        while (iterator.hasNext()) {
            String key = iterator.next();
            String value = configuration.getString(key);
            map.put(key, value);
        }
        String value = (String) this.source.get(name);
        if (value == null) {
            return new Response(map);

        } else {
            return new Response(Collections.singletonMap(name, value));

        }
    }

    @WriteOperation
    public Response update(@Selector String name, @Selector String value) {
        Map<String, Object> source = this.source;
        String oldValue = environment.getProperty(name);
        value = StringUtils.defaultIfBlank(value, "");
        source.put(name, value);
        publisher(name,oldValue, value);
        return get(name);
    }

    @DeleteOperation
    public Response delete(@Selector String name) {
        Map<String, Object> source = this.source;
        String oldValue = environment.getProperty(name);

        source.remove(name);
        String value = environment.getProperty(name);
        publisher(name, oldValue, value);

        return get(name);
    }

    protected void publisher(String name, String oldValue, String value) {
        if (!StringUtils.equals(oldValue, value)) {
            try {
                Set<String> keys = new HashSet<String>();
                keys.add(name);
                this.publisher.publishEvent(new EnvironmentChangeEvent(keys));
            } catch (Exception e) {
                logger.error("EnvironmentChangeEvent Error,name=" + name + ",value=" + value, e);
            }
        }
    }

    @DeleteOperation
    public Response clear() {
        Map<String, Object> map = Collections.unmodifiableMap(this.source);


        Map<String, String> oldValues = new HashMap<>();
        for (String name : map.keySet()) {
            oldValues.put(name, environment.getProperty(name));
        }
        source.clear();
        Set<String> keys = new HashSet<>();
        for (String name : map.keySet()) {
            String value = environment.getProperty(name);
            String oldValue = oldValues.get(name);
            if (!StringUtils.equals(value, oldValue)) {
                keys.add(name);
            }

        }
        if (!keys.isEmpty()) {

            try {
                this.publisher.publishEvent(new EnvironmentChangeEvent(keys));
            } catch (Exception e) {
                logger.error("EnvironmentChangeEvent Error,names=" + StringUtils.join(keys) + "", e);
            }
        }
        return props();
    }

    public static class Response {
        Map contexts;

        public Response(Map contexts) {
            this.contexts = contexts;
        }

        public Map getContexts() {
            return contexts;
        }

        public void setContexts(Map contexts) {
            this.contexts = contexts;
        }
    }

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        this.publisher = applicationEventPublisher;
    }
}
