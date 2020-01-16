package com.github.vlmap.spring.loadbalancer.core.attach;

import com.github.vlmap.spring.loadbalancer.GrayLoadBalancerProperties;
import com.github.vlmap.spring.loadbalancer.core.attach.cli.GaryAttachParamater;
import org.springframework.core.env.Environment;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.HttpCookie;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import javax.validation.constraints.NotNull;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ReactiveAttachHandler extends AttachHandler {

    public ReactiveAttachHandler(GrayLoadBalancerProperties properties, Environment environment) {
        super(properties, environment);
    }

    public Mono<SimpleRequestData> parser(@NotNull List<GaryAttachParamater> attachs, SimpleRequestData data, ServerWebExchange exchange) {
        ServerHttpRequest request = exchange.getRequest();
        data.path = request.getPath().value();
        data.method = request.getMethod().name();
        MediaType contentType = request.getHeaders().getContentType();
        if (contentType != null) {
            data.contentType = contentType.getType() + "/" + contentType.getSubtype();
        }


        data.headers = new LinkedMultiValueMap<>();
        HttpHeaders headers = request.getHeaders();
        if (headers != null) {
            data.headers.addAll(headers);
        }

        data.cookies = new LinkedMultiValueMap<>();
        for (Map.Entry<String, List<HttpCookie>> entry : request.getCookies().entrySet()) {
            String key = entry.getKey();
            List<HttpCookie> values = entry.getValue();
            List<String> list = new ArrayList<>(values.size());
            values.stream().forEach(cookie -> list.add(cookie.getValue()));
            data.cookies.put(key, list);

        }


        Mono<SimpleRequestData> mono = Mono.just(data);

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        data.params = params;
        params.addAll(request.getQueryParams());
        return Mono.just(data).flatMap((object) -> {
            if (MediaType.APPLICATION_FORM_URLENCODED.isCompatibleWith(contentType)) {
                return exchange.getFormData().map(formData -> {
                    params.addAll(formData);
                    return data;
                });
            }
            return Mono.just(data);
        }).flatMap(object -> {
            if (isReadBody(attachs) && MediaType.APPLICATION_JSON.isCompatibleWith(contentType)) {
                return Mono.from(request.getBody().flatMap(dataBuffer -> {

                    Charset charset = contentType.getCharset();
                    charset = charset == null ? AttachHandler.DEFAULT_CHARSET : charset;

                    CharBuffer charBuffer = charset.decode(dataBuffer.asByteBuffer());
                    DataBufferUtils.release(dataBuffer);
                    String json = charBuffer.toString();
                    data.body = json;
                    return  Mono.just(data);
                }));
            }
            return Mono.just(data);

        }).map(object -> object).thenReturn(data);


    }

}
