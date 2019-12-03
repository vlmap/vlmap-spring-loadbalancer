package com.github.vlmap.spring.tools.loadbalancer.platform.springmvc;

import com.github.vlmap.spring.tools.SpringToolsProperties;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.constants.ZuulHeaders;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import com.netflix.zuul.http.HttpServletRequestWrapper;
import com.netflix.zuul.http.ServletInputStreamWrapper;
import com.netflix.zuul.util.HTTPRequestUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.EnumerationUtils;
import org.apache.commons.collections.iterators.EnumerationIterator;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.servlet.filter.OrderedFilter;
import org.springframework.cloud.netflix.zuul.filters.support.FilterConstants;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.net.SocketTimeoutException;
import java.net.URLDecoder;
import java.util.*;
import java.util.zip.*;


public class TagSpringmvcFilter implements OrderedFilter {

    SpringToolsProperties properties;

    public TagSpringmvcFilter(SpringToolsProperties properties) {
        this.properties = properties;
    }


    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        String tag = httpServletRequest.getHeader(this.properties.getTagHeaderName());


        if (StringUtils.isBlank(tag)) {
            tag = properties.getTagLoadbalancer().getHeader();
            if (StringUtils.isNotBlank(tag)) {
                ServletServerHttpRequest servletServerHttpRequest = new ServletServerHttpRequest((HttpServletRequest) request);
                servletServerHttpRequest.
                HttpServletRequestWrapper wrapper = new HttpServletRequestWrapper(httpServletRequest){
                    @Override
                    public Enumeration<String> getHeaderNames() {
                        EnumerationIterator
                        return super.getHeaderNames();
                    }

                    @Override
                    public Enumeration<String> getHeaders(String name) {
                        return super.getHeaders(name);
                    }

                    @Override
                    public String getHeader(String name) {
                        return super.getHeader(name);
                    }
                };

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
}
