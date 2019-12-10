package com.github.vlmap.spring.tools.context;

import com.github.vlmap.spring.tools.SpringToolsProperties;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.boot.context.properties.source.ConfigurationPropertyName;
import org.springframework.cloud.bootstrap.config.PropertySourceLocator;
import org.springframework.core.env.Environment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.PropertySource;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SpringToolsPropertySourceLocator implements PropertySourceLocator {


    @Override
    public PropertySource<?> locate(Environment environment) {
        String propertySourceName = environment.getProperty("spring.tools.property-source-name");
        if (StringUtils.isBlank(propertySourceName)) {
            propertySourceName = SpringToolsProperties.DEFAULT_TOOLS_PROPERTIES_NAME;
        }


        Map<String, Object> map = new ConcurrentHashMap<>();
        SpringToolsProperties properties = new SpringToolsProperties();

        Binder.get(environment).bind(ConfigurationPropertyName.of("spring.tools"), Bindable.ofInstance(properties));

        map.put("spring.tools.zookeeper.enabled", String.valueOf(properties.getZookeeper().isEnabled()));

        map.put("spring.tools.property-source-name", StringUtils.defaultString(properties.getPropertySourceName(), ""));
        map.put("spring.tools.tag-loadbalancer.enabled", String.valueOf(properties.getTagLoadbalancer().isEnabled()));
        map.put("spring.tools.tag-loadbalancer.feign.enabled", String.valueOf(properties.getTagLoadbalancer().getFeign().isEnabled()));
        map.put("spring.tools.tag-loadbalancer.rest-template.enabled", String.valueOf(properties.getTagLoadbalancer().getRestTemplate().isEnabled()));
        map.put("spring.tools.tag-loadbalancer.web-client.enabled", String.valueOf(properties.getTagLoadbalancer().getWebClient().isEnabled()));
        map.put("spring.tools.tag-loadbalancer.header", StringUtils.defaultString(properties.getTagLoadbalancer().getHeader(), ""));

        map.put("spring.tools.tag-loadbalancer.header-name", StringUtils.defaultString(properties.getTagLoadbalancer().getHeaderName(), ""));

        MapPropertySource result = new MapPropertySource(propertySourceName, map);


        return result;
    }
}
