package com.github.vlmap.spring.tools;

import com.github.vlmap.spring.tools.actuator.PropertiesEndPoint;
import com.github.vlmap.spring.tools.context.event.listener.DelegatePropertiesChangeListener;
import com.github.vlmap.spring.tools.context.event.listener.PropertiesListener;
import com.github.vlmap.spring.tools.loadbalancer.platform.gateway.TagLoadBalancerClientFilterProxy;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.autoconfigure.endpoint.condition.ConditionalOnEnabledEndpoint;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.cloud.endpoint.event.RefreshEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

@Configuration
@EnableConfigurationProperties({SpringToolsProperties.class})

public class SpringToolsAutoConfiguration {



    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnEnabledEndpoint
    public PropertiesEndPoint propsEndPoint(Environment environment, SpringToolsProperties properties) {
        return new PropertiesEndPoint(environment, properties);
    }

    @Autowired
    public void refreshListener(ApplicationEventPublisher publisher, DelegatePropertiesChangeListener listener) {
        listener.addListener(new PropertiesListener("spring.application.refresh", false, event -> {
            String value = event.getValue();
            if (BooleanUtils.toBoolean(value)) {
                publisher.publishEvent(new RefreshEvent(this, event, event.getEventDesc()));
            }

        }));
    }

    @Bean
    public DelegatePropertiesChangeListener delegatePropChangeListener() {
        return new DelegatePropertiesChangeListener();
    }

    @Autowired
    public void SpringToolsPropertiesListener(Environment environment, DelegatePropertiesChangeListener listener, SpringToolsProperties properties) {
        listener.addListener(new PropertiesListener("spring.tools", true, (event -> {

            Binder.get(environment).bind("spring.tools", Bindable.ofInstance(properties));


        })));

    }
}
