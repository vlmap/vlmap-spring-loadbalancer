package com.github.vlmap.spring.loadbalancer.core.platform.reactive;

import com.github.vlmap.spring.loadbalancer.GrayLoadBalancerProperties;
import com.github.vlmap.spring.loadbalancer.core.RequestMappingInvoker;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.web.reactive.WebFluxRegistrations;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.result.method.annotation.GrayRequestMappingHandlerAdapter;

/**
 * 保证Controller 方法可以调用 RuntimeManager.get方法
 */
@ConditionalOnWebApplication(type = ConditionalOnWebApplication.Type.REACTIVE)
@EnableConfigurationProperties({GrayLoadBalancerProperties.class})
@ConditionalOnProperty(name = "vlmap.spring.loadbalancer.controller.enabled", matchIfMissing = true)

@Configuration
public class RequestMappingHandlerAdapterConfiguration {

    @Bean
    public WebFluxRegistrations webFluxRegistrations(RequestMappingInvoker requestMappingInvoker) {
        return new GrayWebFluxRegistrations(requestMappingInvoker);
    }

    public static class GrayWebFluxRegistrations implements WebFluxRegistrations {
        private RequestMappingInvoker requestMappingInvoker;

        public GrayWebFluxRegistrations(RequestMappingInvoker requestMappingInvoker) {
            this.requestMappingInvoker = requestMappingInvoker;
        }

        @Override
        public GrayRequestMappingHandlerAdapter getRequestMappingHandlerAdapter() {
            GrayRequestMappingHandlerAdapter handlerAdapter = new GrayRequestMappingHandlerAdapter();
            handlerAdapter.setRequestMappingInvoker(requestMappingInvoker);

            return handlerAdapter;
        }
    }

}
