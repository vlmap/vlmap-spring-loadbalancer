package com.github.vlmap.spring.loadbalancer.core.platform.servlet;

import com.github.vlmap.spring.loadbalancer.GrayLoadBalancerProperties;
import com.github.vlmap.spring.loadbalancer.core.platform.FilterOrder;
import com.github.vlmap.spring.loadbalancer.core.platform.ReadBodyFilter;
import org.springframework.boot.web.servlet.filter.OrderedFilter;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.web.util.ContentCachingRequestWrapper;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;


public class GrayReadBodyServletFilter extends ReadBodyFilter implements OrderedFilter {

    public GrayReadBodyServletFilter(GrayLoadBalancerProperties properties) {
        super(properties);
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;


        MediaType contentType = getContentType(httpServletRequest.getContentType());
        HttpMethod method = HttpMethod.resolve(httpServletRequest.getMethod());
        if (use(contentType, method)) {
            request.setAttribute(READ_BODY_TAG, true);
            httpServletRequest = new ContentCachingRequestWrapper((HttpServletRequest) request);
        }


        chain.doFilter(httpServletRequest, response);


    }


    public MediaType getContentType(String value) {

        return (org.springframework.util.StringUtils.hasLength(value) ? MediaType.parseMediaType(value) : null);
    }

    public int getOrder() {
        return FilterOrder.ORDER_ATTACH_FILTER;
    }

}
