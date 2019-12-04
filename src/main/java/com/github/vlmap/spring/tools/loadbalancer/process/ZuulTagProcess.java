package com.github.vlmap.spring.tools.loadbalancer.process;


import com.github.vlmap.spring.tools.SpringToolsProperties;
import com.github.vlmap.spring.tools.loadbalancer.TagProcess;
import com.netflix.zuul.context.RequestContext;
import org.apache.commons.lang.StringUtils;
import org.springframework.core.annotation.Order;

import javax.servlet.http.HttpServletRequest;

@Order(10)
public class ZuulTagProcess implements TagProcess {
    protected SpringToolsProperties properties;

    public ZuulTagProcess(SpringToolsProperties properties) {
        this.properties = properties;
    }

    @Override
    public String getTag() {
        RequestContext context = RequestContext.getCurrentContext();
        HttpServletRequest request = context.getRequest();
        String header = null;

        if (request != null) {
            header = request.getHeader(this.properties.getTagHeaderName());
        }

        if (StringUtils.isBlank(header)) {
            header = properties.getTagLoadbalancer().getHeader();
        }

        return header;
    }
}
