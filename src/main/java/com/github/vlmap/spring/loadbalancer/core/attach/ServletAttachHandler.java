package com.github.vlmap.spring.loadbalancer.core.attach;

import com.github.vlmap.spring.loadbalancer.GrayLoadBalancerProperties;
import com.github.vlmap.spring.loadbalancer.core.attach.cli.GaryAttachParamater;
import org.apache.commons.collections.EnumerationUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.ArrayUtils;
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

import javax.servlet.ServletInputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotNull;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.util.*;

public class ServletAttachHandler extends AttachHandler {

    public ServletAttachHandler(GrayLoadBalancerProperties properties, Environment environment) {
        super(properties, environment);
    }

    public SimpleRequestData parser(List<GaryAttachParamater> attachs, SimpleRequestData data, HttpServletRequest request) {
        data.path = request.getRequestURI();
        data.method = request.getMethod();

        MediaType contentType = MediaType.valueOf(data.contentType);

         if(contentType!=null){
            data.contentType = contentType.getType()+"/"+contentType.getSubtype();
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

        if (isReadBody(attachs) && MediaType.APPLICATION_JSON.isCompatibleWith(contentType)) {

            Charset charset = contentType.getCharset();
            charset=charset==null?AttachHandler.DEFAULT_CHARSET:charset;

            ServletInputStream input = null;
            try {

                input = request.getInputStream();
                data.body = IOUtils.toString(input, charset);

            } catch (Exception e) {

            } finally {
                IOUtils.closeQuietly(input);
            }


        }

        return null;
    }



}
