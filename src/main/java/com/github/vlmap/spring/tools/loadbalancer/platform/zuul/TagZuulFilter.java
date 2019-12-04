package com.github.vlmap.spring.tools.loadbalancer.platform.zuul;

import com.github.vlmap.spring.tools.SpringToolsProperties;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import org.apache.commons.lang3.StringUtils;
import org.springframework.cloud.netflix.zuul.filters.support.FilterConstants;

import javax.servlet.http.HttpServletRequest;


public class TagZuulFilter extends ZuulFilter {

     SpringToolsProperties properties;

    public TagZuulFilter(SpringToolsProperties properties) {
         this.properties=properties;
    }

    @Override
    public String filterType() {
        return FilterConstants.PRE_TYPE;
    }

    @Override
    public int filterOrder() {
        return 0;
    }

    @Override
    public boolean shouldFilter() {
        return true;
    }

    @Override
    public Object run() throws ZuulException {
        RequestContext context = RequestContext.getCurrentContext();
        HttpServletRequest request= context.getRequest();

        String tag =request.getHeader(this.properties.getTagHeaderName());
        if (StringUtils.isBlank(tag)) {
            tag = properties.getTagLoadbalancer().getHeader();
            if (StringUtils.isNotBlank(tag)) {

                context.addZuulRequestHeader(properties.getTagHeaderName(), tag);


            }
        }
        return null;
    }
}
