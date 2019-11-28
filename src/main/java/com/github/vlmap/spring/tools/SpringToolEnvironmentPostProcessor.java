package com.github.vlmap.spring.tools;

import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.boot.context.properties.source.ConfigurationPropertyName;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.Ordered;
import org.springframework.core.env.*;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SpringToolEnvironmentPostProcessor implements EnvironmentPostProcessor, Ordered {
    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        String propertySourceName=environment.getProperty("spring.tools.property-source-name");
        if(StringUtils.isNotBlank(propertySourceName)){
            environment.getPropertySources().remove(propertySourceName);

        }
        SpringToolsProperties properties = new SpringToolsProperties();
        Binder.get(environment).bind(ConfigurationPropertyName.of("spring.tools"), Bindable.ofInstance(properties));
          propertySourceName = properties.getPropertySourceName();
        if (StringUtils.isBlank(propertySourceName)) return;

        Map<String, Object> map = new ConcurrentHashMap<>();

        MapPropertySource result = new MapPropertySource(propertySourceName, map);




        map.put("spring.tools.zookeeper.enabled", String.valueOf(properties.getZookeeper().isEnabled()));

        map.put("spring.tools.property-source-name", StringUtils.defaultString(properties.getPropertySourceName(), ""));
        map.put("spring.tools.tag-loadbalancer.enabled", String.valueOf(properties.getTagLoadbalancer().isEnabled()));
        map.put("spring.tools.tag-loadbalancer.feign.enabled", String.valueOf(properties.getTagLoadbalancer().getFeign().isEnabled()));
        map.put("spring.tools.tag-loadbalancer.rest-template.enabled", String.valueOf(properties.getTagLoadbalancer().getRestTemplate().isEnabled()));
        map.put("spring.tools.tag-loadbalancer.web-client.enabled", String.valueOf(properties.getTagLoadbalancer().getWebClient().isEnabled()));
        map.put("spring.tools.tag-loadbalancer.header", StringUtils.defaultString(properties.getTagLoadbalancer().getHeader(), ""));
        map.put("spring.tools.tag-loadbalancer.header-name", StringUtils.defaultString(properties.getTagLoadbalancer().getHeaderName(), ""));

        environment.getPropertySources().addLast(result);
    }




    @Override
    public int getOrder() {
        return Ordered.LOWEST_PRECEDENCE;
    }

}
