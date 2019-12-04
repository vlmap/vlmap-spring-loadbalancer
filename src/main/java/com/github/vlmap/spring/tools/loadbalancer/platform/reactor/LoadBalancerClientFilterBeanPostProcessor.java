package com.github.vlmap.spring.tools.loadbalancer.platform.reactor;

import com.github.vlmap.spring.tools.SpringToolsProperties;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.filter.LoadBalancerClientFilter;
import org.springframework.core.Ordered;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

public class LoadBalancerClientFilterBeanPostProcessor implements BeanPostProcessor {
    private SpringToolsProperties properties;

    public LoadBalancerClientFilterBeanPostProcessor(SpringToolsProperties properties) {
        this.properties = properties;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if(bean instanceof LoadBalancerClientFilter){
            return new ProxyLoadBalancerClientFilter(properties,(LoadBalancerClientFilter)bean);
        }
        return bean;
    }
    static class ProxyLoadBalancerClientFilter implements   GlobalFilter, Ordered {
        LoadBalancerClientFilter filter;
        SpringToolsProperties properties;
        public ProxyLoadBalancerClientFilter(SpringToolsProperties properties,LoadBalancerClientFilter filter) {
            this.filter = filter;
            this.properties=properties;
        }

        @Override
        public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
            try {
                String tag = exchange.getRequest().getHeaders().getFirst(this.properties.getTagHeaderName());


                if (StringUtils.isBlank(tag)) {
                    tag = properties.getTagLoadbalancer().getHeader();
                    if (StringUtils.isNotBlank(tag)) {
                        exchange.getRequest().getHeaders().add(this.properties.getTagHeaderName(), tag);
                    }
                }
                ReactiveContextHolder.set(exchange);

                return filter.filter(exchange,chain);
            } finally {

                ReactiveContextHolder.dispose();
            }
        }

        @Override
        public int getOrder() {
            return filter.getOrder();
        }
    }
}
