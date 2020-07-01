package com.github.vlmap.spring.loadbalancer.core.platform.servlet;

import com.github.vlmap.spring.loadbalancer.GrayLoadBalancerProperties;
import com.github.vlmap.spring.loadbalancer.core.GrayMeteData;
import com.github.vlmap.spring.loadbalancer.core.platform.StrictFilter;
import com.github.vlmap.spring.loadbalancer.util.EnvironmentUtils;
import com.github.vlmap.spring.loadbalancer.util.Util;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;


public class StrictServletFilter extends StrictFilter implements Filter {


    public StrictServletFilter(GrayLoadBalancerProperties properties) {
        super(properties);

    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void destroy() {

    }


    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

        Map<String, String> metadata = metadata();
        GrayMeteData object = new GrayMeteData();
        EnvironmentUtils.binder(object, metadata, "");


        if (!object.getStrict().isEnabled()) {
            chain.doFilter(request, response);
            return;
        }
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        HttpServletResponse httpServletResponse = (HttpServletResponse) response;
        String headerName = this.properties.getHeaderName();
        String tag = httpServletRequest.getHeader(headerName);
        /**
         * 严格模式,请求标签不匹配拒绝响应
         */
        String uri = ((HttpServletRequest) request).getRequestURI();
        if (!validate(uri, tag)) {
            String message = getMessage();
            int code = getCode();
            if (logger.isTraceEnabled()) {
                logger.trace("The server is strict model,current request Header[" + headerName + ":" + tag + "] don't match \"[" + StringUtils.join(getGrayTags(), ",") + "]\",response code:" + code);
            }

            if (StringUtils.isBlank(message)) {
                httpServletResponse.setStatus(code);

            } else {
                httpServletResponse.sendError(code, message);
            }
            return;
        }
        chain.doFilter(request, response);


    }


}
