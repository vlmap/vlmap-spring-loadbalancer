package com.github.vlmap.spring.loadbalancer.core.client.webclient;

import com.github.vlmap.spring.loadbalancer.GrayLoadBalancerProperties;
import com.github.vlmap.spring.loadbalancer.runtime.ContextManager;
import com.github.vlmap.spring.loadbalancer.runtime.RuntimeContext;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.ExchangeFunction;
import reactor.core.publisher.Mono;

public class GrayWebClientInterceptor implements ExchangeFilterFunction {

    @Autowired

    private GrayLoadBalancerProperties properties;


    @Override
    public Mono<ClientResponse> filter(ClientRequest request, ExchangeFunction next) {

        HttpHeaders headers = request.headers();
        String headerName = properties.getHeaderName();
        String header = headers.getFirst(headerName);
        String tag = header;
        if (StringUtils.isBlank(tag)) {
            tag = ContextManager.getRuntimeContext().get(RuntimeContext.REQUEST_TAG_REFERENCE, String.class);

        }

        if (StringUtils.isNotBlank(tag) && !StringUtils.equals(tag, header)) {
            request = ClientRequest.from(request).header(headerName, tag).build();

        }

        try {
            ContextManager.getRuntimeContext().put(RuntimeContext.REQUEST_TAG_REFERENCE, tag);
            return next.exchange(request);

        } finally {
            ContextManager.getRuntimeContext().onComplete();

        }

    }
}
