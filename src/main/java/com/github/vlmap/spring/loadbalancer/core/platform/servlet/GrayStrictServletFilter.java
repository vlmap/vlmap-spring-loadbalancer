package com.github.vlmap.spring.loadbalancer.core.platform.servlet;

import com.github.vlmap.spring.loadbalancer.GrayLoadBalancerProperties;
import com.github.vlmap.spring.loadbalancer.core.StrictHandler;
import com.github.vlmap.spring.loadbalancer.core.platform.FilterOrder;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.servlet.filter.OrderedFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


public class GrayStrictServletFilter implements OrderedFilter {
    Logger logger = LoggerFactory.getLogger(this.getClass());

    GrayLoadBalancerProperties properties;
    StrictHandler strictHandler;

    public GrayStrictServletFilter(GrayLoadBalancerProperties properties, StrictHandler strictHandler) {
        this.properties = properties;
        this.strictHandler = strictHandler;
    }


    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        if (!properties.getStrict().isEnabled()) {
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
        if (!strictHandler.validate(uri, tag)) {
            String message = strictHandler.getMessage();
            int code = strictHandler.getCode();
            if (logger.isInfoEnabled()) {
                logger.info("The server is strict model,current request Header[" + headerName + ":" + tag + "] don't match \"[" + StringUtils.join(strictHandler.getGrayTags(), ",") + "]\",response code:" + code);
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

    @Override
    public int getOrder() {
        return FilterOrder.ORDER_STRICT_FILTER;
    }

}
