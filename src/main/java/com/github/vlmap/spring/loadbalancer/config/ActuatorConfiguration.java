package com.github.vlmap.spring.loadbalancer.config;

import com.github.vlmap.spring.loadbalancer.actuate.GrayEndpoint;
import com.github.vlmap.spring.loadbalancer.actuate.GrayOldEndpoint;
import com.github.vlmap.spring.loadbalancer.actuate.GrayParamater;
import com.github.vlmap.spring.loadbalancer.core.platform.StrictFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.endpoint.AbstractEndpoint;
import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.mvc.MvcEndpoint;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Configuration
@ConditionalOnProperty(
        value = {"vlmap.spring.loadbalancer.actuator.enabled"},
        matchIfMissing = true
)

public class ActuatorConfiguration {
    @Bean

    public GrayParamater GrayParamater() {
        return new GrayParamater();
    }

    @Configuration
    @ConditionalOnClass(AbstractEndpoint.class)
    static class Actuator_1_Configuration {
        @Bean
        public GrayOldEndpoint loadbalancerEndpoint() {
            return new GrayOldEndpoint();
        }
    }

    @Configuration
    @ConditionalOnClass({Endpoint.class})
    static class Actuator_2_Configuration {
        @Bean
        public GrayEndpoint loadbalancerEndpoint() {
            return new GrayEndpoint();
        }
    }


    @Configuration
    @ConditionalOnClass(MvcEndpoint.class)
    public static class ActuratorConfiguration {
        @Autowired
        private Collection<MvcEndpoint> endpoints;
        @Autowired
        private StrictFilter filter;

        /**
         * spring boot1.5    版本 endpoint
         */
        @PostConstruct
        public void initMethod() {
            List<String> list = new ArrayList<String>();
            for (MvcEndpoint endpoint : endpoints) {
                list.add(endpoint.getPath());
            }
            List<String> temp = filter.getIgnores();
            if (temp != null) {
                list.addAll(temp);
            }
            filter.setIgnores(list);
        }
    }
}
