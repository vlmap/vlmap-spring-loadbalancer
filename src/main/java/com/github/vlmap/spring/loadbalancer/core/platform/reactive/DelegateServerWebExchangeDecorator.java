package com.github.vlmap.spring.loadbalancer.core.platform.reactive;

import org.springframework.core.ResolvableType;
import org.springframework.core.codec.Hints;
import org.springframework.http.InvalidMediaTypeException;
import org.springframework.http.MediaType;
import org.springframework.http.codec.HttpMessageReader;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.http.codec.multipart.Part;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.util.CollectionUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.ServerWebExchangeDecorator;
import reactor.core.publisher.Mono;

public class DelegateServerWebExchangeDecorator extends ServerWebExchangeDecorator {

    private static final ResolvableType FORM_DATA_TYPE =
            ResolvableType.forClassWithGenerics(MultiValueMap.class, String.class, String.class);

    private static final ResolvableType MULTIPART_DATA_TYPE = ResolvableType.forClassWithGenerics(
            MultiValueMap.class, String.class, Part.class);

    private static final Mono<MultiValueMap<String, String>> EMPTY_FORM_DATA =
            Mono.just(org.springframework.util.CollectionUtils.unmodifiableMultiValueMap(new LinkedMultiValueMap<String, String>(0)))
                    .cache();

    private static final Mono<MultiValueMap<String, Part>> EMPTY_MULTIPART_DATA =
            Mono.just(CollectionUtils.unmodifiableMultiValueMap(new LinkedMultiValueMap<String, Part>(0)))
                    .cache();


    private ServerCodecConfigurer codecConfigurer = ServerCodecConfigurer.create();


    private Mono<MultiValueMap<String, String>> formDataMono;
    private Mono<MultiValueMap<String, Part>> multipartDataMono;

    public DelegateServerWebExchangeDecorator(ServerWebExchange delegate) {
        this(delegate, null);

    }

    public DelegateServerWebExchangeDecorator(ServerWebExchange delegate, ServerCodecConfigurer codecConfigurer) {
        super(delegate);
        if (codecConfigurer != null) {
            this.codecConfigurer = codecConfigurer;
        }
        formDataMono = initFormData(delegate.getRequest(), this.codecConfigurer, this.getLogPrefix());

        multipartDataMono = initMultipartData(delegate.getRequest(), this.codecConfigurer, this.getLogPrefix());

    }


    @SuppressWarnings("unchecked")
    private static Mono<MultiValueMap<String, String>> initFormData(ServerHttpRequest request,
                                                                    ServerCodecConfigurer configurer, String logPrefix) {

        try {
            MediaType contentType = request.getHeaders().getContentType();
            if (MediaType.APPLICATION_FORM_URLENCODED.isCompatibleWith(contentType)) {
                return ((HttpMessageReader<MultiValueMap<String, String>>) configurer.getReaders().stream()
                        .filter(reader -> reader.canRead(FORM_DATA_TYPE, MediaType.APPLICATION_FORM_URLENCODED))
                        .findFirst()
                        .orElseThrow(() -> new IllegalStateException("No form data HttpMessageReader.")))
                        .readMono(FORM_DATA_TYPE, request, Hints.from(Hints.LOG_PREFIX_HINT, logPrefix))
                        .switchIfEmpty(EMPTY_FORM_DATA)
                        .cache();
            }
        } catch (InvalidMediaTypeException ex) {
            // Ignore
        }
        return EMPTY_FORM_DATA;
    }


    @SuppressWarnings("unchecked")
    private static Mono<MultiValueMap<String, Part>> initMultipartData(ServerHttpRequest request,
                                                                       ServerCodecConfigurer configurer, String logPrefix) {

        try {
            MediaType contentType = request.getHeaders().getContentType();
            if (MediaType.MULTIPART_FORM_DATA.isCompatibleWith(contentType)) {
                return ((HttpMessageReader<MultiValueMap<String, Part>>) configurer.getReaders().stream()
                        .filter(reader -> reader.canRead(MULTIPART_DATA_TYPE, MediaType.MULTIPART_FORM_DATA))
                        .findFirst()
                        .orElseThrow(() -> new IllegalStateException("No multipart HttpMessageReader.")))
                        .readMono(MULTIPART_DATA_TYPE, request, Hints.from(Hints.LOG_PREFIX_HINT, logPrefix))
                        .switchIfEmpty(EMPTY_MULTIPART_DATA)
                        .cache();
            }
        } catch (InvalidMediaTypeException ex) {
            // Ignore
        }
        return EMPTY_MULTIPART_DATA;
    }


    /**
     * 防止读取原始DataBuffer
     *
     * @return
     */
    @Override
    public Mono<MultiValueMap<String, String>> getFormData() {
        return this.formDataMono;
    }

    /**
     * 防止读取原始DataBuffer
     *
     * @return
     */
    @Override
    public Mono<MultiValueMap<String, Part>> getMultipartData() {
        return this.multipartDataMono;
    }
}
