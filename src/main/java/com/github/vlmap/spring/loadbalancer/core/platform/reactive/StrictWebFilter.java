package com.github.vlmap.spring.loadbalancer.core.platform.reactive;

import com.github.vlmap.spring.loadbalancer.GrayLoadBalancerProperties;
import com.github.vlmap.spring.loadbalancer.core.CurrentServer;
import com.github.vlmap.spring.loadbalancer.core.platform.FilterOrder;
import com.github.vlmap.spring.loadbalancer.core.platform.StrictFilter;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

public class StrictWebFilter extends StrictFilter implements WebFilter {

    Logger logger = LoggerFactory.getLogger(this.getClass());

    public StrictWebFilter(GrayLoadBalancerProperties properties, CurrentServer currentServer) {
        super(properties,currentServer);

    }
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        if (!properties.getStrict().isEnabled()) {
            return chain.filter(exchange);
        }
        String headerName = this.properties.getHeaderName();

        String tag = exchange.getRequest().getHeaders().getFirst(headerName);


        String uri = exchange.getRequest().getPath().value();

        /**
         * 严格模式,请求标签不匹配拒绝响应
         */
        if (!validate(uri, tag)) {
            String message = getMessage();
            int code = getCode();
            if (logger.isInfoEnabled()) {

                logger.info("The server is strict model,current request Header[" + headerName + ":" + tag + "] don't match \"[" + StringUtils.join(getGrayTags(), ",") + "]\",response code:" + code);

            }
            HttpStatus status = HttpStatus.valueOf(code);
            if (StringUtils.isBlank(message)) {
                throw new ResponseStatusException(status);

            } else {
                throw new ResponseStatusException(status, message);
            }

        }


        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return FilterOrder.ORDER_STRICT_FILTER;
    }

}
