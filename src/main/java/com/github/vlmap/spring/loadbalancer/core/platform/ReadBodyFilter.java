package com.github.vlmap.spring.loadbalancer.core.platform;

import com.github.vlmap.spring.loadbalancer.GrayLoadBalancerProperties;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;

import java.util.List;

public abstract class ReadBodyFilter {
    protected GrayLoadBalancerProperties properties;
    public static final String READ_BODY_TAG = "__ReadBodyTag__";

    public ReadBodyFilter(GrayLoadBalancerProperties properties) {
        this.properties = properties;
    }

    protected boolean use(MediaType contentType, HttpMethod method) {
        if (HttpMethod.GET.equals(method) || HttpMethod.HEAD.equals(method)) {
            return false;
        }
        if (contentType != null) {
            List<MediaType> cacheBodyContentType = properties.getAttach().getCacheBodyContentType();

            if (cacheBodyContentType != null) {
                for (MediaType type : cacheBodyContentType) {
                    if (type.isCompatibleWith(contentType)) {
                        return true;

                    }
                }
            }

        }
        return false;

    }
}
