package com.github.vlmap.spring.loadbalancer.util;

import com.github.vlmap.spring.loadbalancer.GrayLoadBalancerProperties;
import com.github.vlmap.spring.loadbalancer.core.platform.SimpleRequest;
import com.jayway.jsonpath.Configuration;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.util.MultiValueMap;

import java.util.List;

public class RequestUtils {
    private static Logger logger = LoggerFactory.getLogger(RequestUtils.class);

    public static MediaType getContentType(String value) {
        return StringUtils.isNotEmpty(value) ? MediaType.parseMediaType(value) : null;
    }

    public static Object getJsonDocument(SimpleRequest data) {
        String body = data.getBody();
        try {

            if (StringUtils.isNotBlank(body)) {
                MultiValueMap<String, String> headers = data.getHeaders();
                if (headers != null) {
                    String value = headers.getFirst(HttpHeaders.CONTENT_TYPE);

                    MediaType contentType = getContentType(value);
                    if (contentType != null && contentType.isCompatibleWith(MediaType.APPLICATION_JSON)) {


                        return Configuration.defaultConfiguration().jsonProvider().parse(body);
                    }
                }

            }
        } catch (Exception e) {
            logger.error("parse json error,json:" + body);
        }

        return null;
    }

    public static boolean useBody(GrayLoadBalancerProperties properties, MediaType contentType, HttpMethod method, long length) {
        if (HttpMethod.GET.equals(method) || HttpMethod.HEAD.equals(method)) {
            return false;
        }
        long maxLength = properties.getCacheBody().getMaxLength();
        if (maxLength == -1) {
            return true;
        } else if (length != -1 && maxLength > length) {
            return true;

        }
        if (contentType != null) {
            List<MediaType> cacheBodyContentType = properties.getCacheBody().getCacheBodyContentType();

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
