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
import org.springframework.web.util.ContentCachingRequestWrapper;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.nio.charset.Charset;
import java.util.*;

public class ServletAttachHandler extends AttachHandler {

    public ServletAttachHandler(GrayLoadBalancerProperties properties, Environment environment) {
        super(properties, environment);
    }

    public SimpleRequestData parser(SimpleRequestData data, HttpServletRequest request) {
        data.path = request.getRequestURI();
        data.method = request.getMethod();

        MediaType contentType = MediaType.valueOf(data.contentType);

        if (contentType != null) {
            data.contentType = contentType.getType() + "/" + contentType.getSubtype();
        }

        Cookie[] cookies = request.getCookies();
        if (ArrayUtils.isNotEmpty(cookies)) {
            data.cookies = new LinkedMultiValueMap<>();
            for (Cookie cookie : cookies) {
                String key = cookie.getName();
                String value = cookie.getValue();

                data.cookies.add(key, value);

            }
        }


        data.params = new LinkedMultiValueMap<>();
        for (Map.Entry<String, String[]> entry : request.getParameterMap().entrySet()) {
            data.params.put(entry.getKey(), Arrays.asList(entry.getValue()));

        }


        Enumeration<String> headerNames = request.getHeaderNames();
        data.headers = new LinkedMultiValueMap<>();
        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            List<String> list = EnumerationUtils.toList(request.getHeaders(headerName));
            data.headers.put(headerName, list);
        }

        if (isJsonRequest(contentType, HttpMethod.resolve(request.getMethod()))) {
            if(ObjectUtils.equals(request.getAttribute(ReadBodyFilter.READ_BODY_TAG), Boolean.TRUE)){
                ContentCachingRequestWrapper wrapper = (ContentCachingRequestWrapper) request;
                Charset charset = contentType.getCharset();
                charset = charset == null ? AttachHandler.DEFAULT_CHARSET : charset;
                byte[] bytes = wrapper.getContentAsByteArray();

                data.body = new String(bytes, charset);
            }

        }

        return null;
    }


}
