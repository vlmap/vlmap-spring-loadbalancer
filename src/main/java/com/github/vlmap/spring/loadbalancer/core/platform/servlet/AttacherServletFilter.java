package com.github.vlmap.spring.loadbalancer.core.platform.servlet;

import com.github.vlmap.spring.loadbalancer.GrayLoadBalancerProperties;
import com.github.vlmap.spring.loadbalancer.core.platform.*;
import com.github.vlmap.spring.loadbalancer.util.RequestUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.EnumerationUtils;
import org.apache.commons.collections.IteratorUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.ContentCachingRequestWrapper;

import javax.servlet.*;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.*;


public class AttacherServletFilter extends AttacherFilter implements Filter {
    private Logger logger = LoggerFactory.getLogger(this.getClass());


    public AttacherServletFilter(GrayLoadBalancerProperties properties) {

        super(properties);

    }


    public SimpleRequest parser(SimpleRequest data, HttpServletRequest request) {
        data.setPath(request.getRequestURI());
        data.setMethod(request.getMethod());


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


        if (ObjectUtils.equals(request.getAttribute(ReadBodyFilter.READ_BODY_TAG), Boolean.TRUE)) {
            ContentCachingRequestWrapper wrapper = (ContentCachingRequestWrapper) request;
            String charsetName = request.getCharacterEncoding();

            Charset charset = null;
            if (StringUtils.isNotEmpty(charsetName)) {
                charset = Charset.forName(charsetName);
            }
            charset = charset == null ? StandardCharsets.UTF_8 : charset;
            byte[] bytes = wrapper.getContentAsByteArray();

            data.setBody(new String(bytes, charset));
        }


        return data;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {


        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        String headerName = properties.getHeaderName();
        String tag = httpServletRequest.getHeader(headerName);

        if (!this.properties.getAttacher().isEnabled() || StringUtils.isNotBlank(tag)) {
            chain.doFilter(request, response);
            return;
        }

        List<RequestMatchParamater> paramaters = super.paramaters;
        SimpleRequest data = new SimpleRequest();


        if (CollectionUtils.isNotEmpty(paramaters)) {
            try {
                initData(httpServletRequest, data);
                Object jsonDocument = RequestUtils.getJsonDocument(data);
                RequestMatchParamater paramater = this.matcher.match(data, jsonDocument, paramaters);
                if (paramater != null) {
                    String value = paramater.getValue();
                    MultiValueMap<String, String> values = new LinkedMultiValueMap<>();
                    values.add(properties.getHeaderName(), value);
                    httpServletRequest = addHeader(httpServletRequest, values);

                }

            } catch (Exception e) {
                logger.error("attach match error", e);
            }


        }
        chain.doFilter(httpServletRequest, response);


    }

    protected HttpServletRequest addHeader(HttpServletRequest httpServletRequest, MultiValueMap<String, String> values) {
        Enumeration<String> headerNames = httpServletRequest.getHeaderNames();
        MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();

        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            List<String> list = EnumerationUtils.toList(httpServletRequest.getHeaders(headerName));
            headers.put(headerName, list);
        }
        headers.addAll(values);
        return new HttpServletRequestWrapper(httpServletRequest) {
            @Override
            public String getHeader(String name) {
                return headers.getFirst(name);
            }

            @Override
            public Enumeration<String> getHeaderNames() {
                return IteratorUtils.asEnumeration(headers.keySet().iterator());
            }

            @Override
            public Enumeration<String> getHeaders(String name) {
                List<String> list = headers.getOrDefault(name, Collections.emptyList());
                return IteratorUtils.asEnumeration(list.iterator());

            }
        };
    }

    /**
     * 收集参数
     *
     * @param
     * @return
     */
    protected SimpleRequest initData(HttpServletRequest httpServletRequest, SimpleRequest data) {
        return parser(data, httpServletRequest);
    }


    public int getOrder() {
        return FilterOrder.ORDER_ATTACH_FILTER;
    }

}
