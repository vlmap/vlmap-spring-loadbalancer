package com.github.vlmap.spring.loadbalancer.core.platform.servlet;

import com.github.vlmap.spring.loadbalancer.GrayLoadBalancerProperties;
import com.github.vlmap.spring.loadbalancer.core.platform.ReadBodyFilter;
import com.github.vlmap.spring.loadbalancer.util.RequestUtils;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.web.util.ContentCachingRequestWrapper;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;


public class ReadBodyServletFilter extends ReadBodyFilter implements Filter {

    public ReadBodyServletFilter(GrayLoadBalancerProperties properties) {
        super(properties);
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;

        if (properties.getCacheBody().isEnabled()) {


            MediaType contentType = RequestUtils.getContentType(httpServletRequest.getContentType());
            HttpMethod method = HttpMethod.resolve(httpServletRequest.getMethod());

            if (RequestUtils.useBody(properties, contentType, method, request.getContentLengthLong())) {
                if (logger.isTraceEnabled()) {
                    logger.trace(" apply cacheBody");
                }

                request.setAttribute(READ_BODY_TAG, true);
                httpServletRequest = new ContentCachingRequestWrapper((HttpServletRequest) request);
            }
            chain.doFilter(httpServletRequest, response);
            return ;
        }
        chain.doFilter(request, response);


    }


}
