package com.github.vlmap.spring.loadbalancer.core.platform.servlet;

import com.github.vlmap.spring.loadbalancer.GrayLoadBalancerProperties;
import com.github.vlmap.spring.loadbalancer.core.platform.FilterOrder;
import com.github.vlmap.spring.loadbalancer.runtime.ContextManager;
import com.github.vlmap.spring.loadbalancer.runtime.RuntimeContext;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;


public class RuntimeRouteTagFilter implements Filter, Ordered {


    protected Logger logger = LoggerFactory.getLogger(this.getClass());

    protected GrayLoadBalancerProperties properties;

    public RuntimeRouteTagFilter(GrayLoadBalancerProperties properties) {
        this.properties = properties;
    }


    @Override
    public int getOrder() {
        return FilterOrder.RUNTIME_CONTEXT_TAG_FILTER;
    }


    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        String headerName = this.properties.getHeaderName();
        String tag = httpServletRequest.getHeader(headerName);
        RuntimeContext runtimeContext = ContextManager.getRuntimeContext();
        ;
        try {
            if (StringUtils.isNotBlank(tag)) {

                runtimeContext.put(RuntimeContext.REQUEST_TAG_REFERENCE, tag);
            }

            chain.doFilter(request, response);

        } finally {
            if (runtimeContext != null) {
                runtimeContext.release();
            }

        }

    }


}
