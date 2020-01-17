package com.github.vlmap.spring.loadbalancer.core.platform.servlet;

import com.github.vlmap.spring.loadbalancer.GrayLoadBalancerProperties;
import com.github.vlmap.spring.loadbalancer.core.platform.FilterOrder;
import com.github.vlmap.spring.loadbalancer.runtime.ContextManager;
import com.github.vlmap.spring.loadbalancer.runtime.RuntimeContext;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.servlet.filter.OrderedFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;


public class GrayLoadBalancerContextServletFilter implements OrderedFilter {
    Logger logger = LoggerFactory.getLogger(this.getClass());

    GrayLoadBalancerProperties properties;

    public GrayLoadBalancerContextServletFilter(GrayLoadBalancerProperties properties) {
        this.properties = properties;
    }


    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        String headerName = this.properties.getHeaderName();
        String tag = httpServletRequest.getHeader(headerName);


        try {

            ContextManager.getRuntimeContext().put(RuntimeContext.SERVLET_REQUEST, request);
            ContextManager.getRuntimeContext().put(RuntimeContext.SERVLET_RESPONSE, response);
            if (StringUtils.isNotBlank(tag)) {
                ContextManager.getRuntimeContext().put(RuntimeContext.REQUEST_TAG_REFERENCE, tag);

            }
            chain.doFilter(request, response);

        } finally {
            ContextManager.getRuntimeContext().onComplete();
        }

    }

    @Override
    public int getOrder() {
        return FilterOrder.ORDER_LOAD_BALANCER_CLIENT_FILTER;
    }

}
