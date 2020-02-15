package com.github.vlmap.spring.loadbalancer.core.attach;

import com.github.vlmap.spring.loadbalancer.GrayLoadBalancerProperties;
import com.github.vlmap.spring.loadbalancer.core.platform.ReadBodyFilter;
import org.apache.commons.collections.EnumerationUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.ContentCachingRequestWrapper;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;

public class ServletAttachHandler extends AbstractAttachHandler {

    public ServletAttachHandler(GrayLoadBalancerProperties properties, Environment environment) {
        super(properties, environment);
    }

    public SimpleRequestData parser(SimpleRequestData data, HttpServletRequest request) {
        data.setPath(request.getRequestURI());
        data.setMethod(request.getMethod());
        data.setContentType(request.getContentType());
        MediaType contentType = MediaType.valueOf(data.getContentType());


        Cookie[] cookies = request.getCookies();
        if (ArrayUtils.isNotEmpty(cookies)) {
            MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
            data.setCookies(map);
            for (Cookie cookie : cookies) {
                String key = cookie.getName();
                String value = cookie.getValue();

                map.add(key, value);

            }
        }

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();

        data.setParams(map);
        for (Map.Entry<String, String[]> entry : request.getParameterMap().entrySet()) {
            map.put(entry.getKey(), Arrays.asList(entry.getValue()));

        }


        Enumeration<String> headerNames = request.getHeaderNames();
        map = new LinkedMultiValueMap<>();
        data.setHeaders(map);
        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            List<String> list = EnumerationUtils.toList(request.getHeaders(headerName));
            map.put(headerName, list);
        }

        if (isJsonRequest(contentType, HttpMethod.resolve(request.getMethod()))) {
            if (ObjectUtils.equals(request.getAttribute(ReadBodyFilter.READ_BODY_TAG), Boolean.TRUE)) {
                ContentCachingRequestWrapper wrapper = (ContentCachingRequestWrapper) request;
                Charset charset = contentType.getCharset();
                charset = charset == null ? AbstractAttachHandler.DEFAULT_CHARSET : charset;
                byte[] bytes = wrapper.getContentAsByteArray();

                data.setBody(new String(bytes, charset));
            }

        }

        return null;
    }


}
