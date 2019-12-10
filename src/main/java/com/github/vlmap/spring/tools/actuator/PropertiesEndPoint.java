package com.github.vlmap.spring.tools.actuator;

import com.github.vlmap.spring.tools.SpringToolsProperties;
import com.github.vlmap.spring.tools.common.PropertiesUtils;
import com.github.vlmap.spring.tools.context.event.PropertyChangeEvent;
import com.netflix.config.ConfigurationManager;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.actuate.endpoint.annotation.*;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.core.env.Environment;
import org.springframework.core.env.MapPropertySource;

import javax.annotation.PostConstruct;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

@Endpoint(
        id = "props"
)
public class PropertiesEndPoint implements ApplicationEventPublisherAware {
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
        String oldValue = (String) source.get(name);
        value = StringUtils.defaultIfBlank(value, "");
        source.put(name, value);
        if (!StringUtils.equals(oldValue, value)) {
            this.publisher.publishEvent(new PropertyChangeEvent(this, name, value, ""));

        }
        return get(name);
    }

    @DeleteOperation
    public Response delete(@Selector String name) {
        Map<String, Object> source = this.source;
        String oldValue = (String) source.get(name);
        source.remove(name);
        if (!StringUtils.equals(oldValue, null)) {
            this.publisher.publishEvent(new PropertyChangeEvent(this, name, null, ""));

        }

        return get(name);
    }

    @DeleteOperation
    public Response clean() {
        Map<String, Object> source = this.source;
        source.clear();

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
