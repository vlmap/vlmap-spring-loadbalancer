package com.github.vlmap.spring.tools.loadbalancer.platform.springmvc;

import com.github.vlmap.spring.tools.SpringToolsProperties;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.EnumerationUtils;
import org.apache.commons.collections.iterators.IteratorEnumeration;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.web.servlet.filter.OrderedFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.*;


public class TagSpringmvcFilter implements OrderedFilter {

    SpringToolsProperties properties;

    public TagSpringmvcFilter(SpringToolsProperties properties) {
        this.properties = properties;
    }


    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        String name = this.properties.getTagHeaderName();
        String tag = httpServletRequest.getHeader(name);


        if (StringUtils.isBlank(tag)) {
            tag = properties.getTagLoadbalancer().getHeader();
            if (StringUtils.isNotBlank(tag)) {

                Map<String, List<String>> headers = getHeaders(httpServletRequest);

                addHeader(headers, name, tag);

                HttpServletRequestWrapper wrapper = new HttpServletRequestWrapper(httpServletRequest, headers);

                chain.doFilter(wrapper, response);
                return;

            }
        }
        chain.doFilter(request, response);

    }

    @Override
    public int getOrder() {
        return 0;
    }

    protected void addHeader(Map<String, List<String>> headers, String name, String value) {

        List<String> values = headers.get(name);
        if (values == null) {
            values = new ArrayList<>();
            headers.put(name, values);

        }
        values.add(value);
    }

    protected Map<String, List<String>> getHeaders(HttpServletRequest httpServletRequest) {

        Enumeration<String> enumeration = httpServletRequest.getHeaderNames();
        Map<String, List<String>> headers = new HashMap<>();
        while (enumeration.hasMoreElements()) {
            String headerName = enumeration.nextElement();
            headers.put(headerName, EnumerationUtils.toList(httpServletRequest.getHeaders(headerName)));
        }
        return headers;

    }

    private static class HttpServletRequestWrapper extends javax.servlet.http.HttpServletRequestWrapper {
        Map<String, List<String>> headers;

        public HttpServletRequestWrapper(HttpServletRequest request, Map<String, List<String>> headers) {
            super(request);
            this.headers = headers;
        }

        @Override
        public Enumeration<String> getHeaderNames() {


            return new IteratorEnumeration(headers.keySet().iterator());
        }

        @Override
        public Enumeration<String> getHeaders(String name) {
            List<String> headerValues = headers.get(name);
            headerValues = headerValues == null ? Collections.emptyList() : headerValues;

            return new IteratorEnumeration(headerValues.iterator());
        }

        @Override
        public String getHeader(String name) {
            List<String> headerValues = headers.get(name);
            if (CollectionUtils.isEmpty(headerValues)) {
                return null;
            }
            return headerValues.get(0);
        }
    }
}
