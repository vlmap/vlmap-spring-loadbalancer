package com.github.vlmap.spring.loadbalancer.core.platform.reactive;

import com.github.vlmap.spring.loadbalancer.GrayLoadBalancerProperties;
import com.github.vlmap.spring.loadbalancer.core.StrictHandler;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.reactive.filter.OrderedWebFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

public class GrayStrictReactiveWebFilter implements OrderedWebFilter {
    private GrayLoadBalancerProperties properties;
    Logger logger = LoggerFactory.getLogger(this.getClass());
    StrictHandler strictHandler;

    public GrayStrictReactiveWebFilter(GrayLoadBalancerProperties properties, StrictHandler strictHandler) {

        this.properties = properties;
        this.strictHandler = strictHandler;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        String headerName = this.properties.getHeaderName();

        String tag = exchange.getRequest().getHeaders().getFirst(headerName);


        String uri = exchange.getRequest().getPath().value();

        /**
         * 严格模式,请求标签不匹配拒绝响应
         */
        if (!strictHandler.validate(uri, tag)) {
            String message = strictHandler.getMessage();
            int code = strictHandler.getCode();
            if (logger.isInfoEnabled()) {

                logger.info("The server is strict model,current request Header[" + headerName + ":" + tag + "] don't match \"[" + StringUtils.join(strictHandler.getGrayTags(), ",") + "]\",response code:" + code);

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
        return Ordered.HIGHEST_PRECEDENCE;
    }
}
