package com.github.vlmap.spring.loadbalancer.core.platform.reactive;

import com.github.vlmap.spring.loadbalancer.GrayLoadBalancerProperties;
import com.github.vlmap.spring.loadbalancer.core.GrayMeteData;
import com.github.vlmap.spring.loadbalancer.core.platform.StrictFilter;
import com.github.vlmap.spring.loadbalancer.util.EnvironmentUtils;
import com.github.vlmap.spring.loadbalancer.util.Util;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.Map;

public class StrictWebFilter extends StrictFilter implements WebFilter {


    public StrictWebFilter(GrayLoadBalancerProperties properties) {
        super(properties);

    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        Map<String, String> metadata = metadata();
        GrayMeteData object = new GrayMeteData();
        EnvironmentUtils.binder(object, metadata, "");


        if (!object.getStrict().isEnabled()) {
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
            if (logger.isTraceEnabled()) {

                logger.trace("The server is strict model,current request Header[" + headerName + ":" + tag + "] don't match \"[" + StringUtils.join(getGrayTags(), ",") + "]\",response code:" + code);

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


}
