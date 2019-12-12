package com.github.vlmap.spring.tools.loadbalancer.platform.gateway;

import com.github.vlmap.spring.tools.SpringToolsProperties;
import com.github.vlmap.spring.tools.common.AntPathMatcherUtils;
import com.github.vlmap.spring.tools.loadbalancer.platform.Platform;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.reactive.filter.OrderedWebFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

public class TagCompatibleReactiveWebFilter implements OrderedWebFilter {
    private SpringToolsProperties properties;
    Logger logger = LoggerFactory.getLogger(this.getClass());

    public TagCompatibleReactiveWebFilter(SpringToolsProperties properties) {
        this.properties = properties;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        String headerName = this.properties.getTagHeaderName();
        String tag = null;
        if (exchange != null) {
            tag = exchange.getRequest().getHeaders().getFirst(headerName);
        }

        String serverTag = properties.getTagLoadbalancer().getHeader();
        /**
         * 非兼容模式,请求标签不匹配拒绝响应
         */
        SpringToolsProperties.Compatible compatible = properties.getCompatible();

        if (!Platform.getInstnce().isGatewayService() && !compatible.isEnabled() && org.apache.commons.lang3.StringUtils.isNotBlank(serverTag) && !org.apache.commons.lang3.StringUtils.equals(tag, serverTag)) {
            String uri = exchange.getRequest().getURI().toString();

            boolean state = AntPathMatcherUtils.matcher(compatible.ignoreUrls(), uri);
            if (!state && compatible.isEnableDefaultIgnoreUrl()) {
                state = AntPathMatcherUtils.matcher(SpringToolsProperties.Compatible.defaultIgnoreUrls(), uri);
            }
            if (!state) {
                if (logger.isInfoEnabled()) {
                    logger.info("The server is compatible model,current request Header[" + headerName + ":" + tag + "] don't match \"" + serverTag + "\",response code:" + compatible.getCode());

                }
                String message = compatible.getMessage();
                HttpStatus status = HttpStatus.valueOf(compatible.getCode());
                if (org.apache.commons.lang3.StringUtils.isBlank(message)) {
                    throw new ResponseStatusException(status);

                } else {
                    throw new ResponseStatusException(status, message);
                }
            }
        }
        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }
}
