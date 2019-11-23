package com.github.vlmap.spring.tools.loadbalancer.platform.zuul;

import com.github.vlmap.spring.tools.loadbalancer.TagProcess;
import com.github.vlmap.spring.tools.loadbalancer.process.ZuulTagProcess;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.exception.ZuulException;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.netflix.zuul.filters.support.FilterConstants;

public class TagZuulFilter extends ZuulFilter {
    @Autowired
    private ZuulTagProcess process;

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
        String tag=process.getRequestTag();
        if(StringUtils.isBlank(tag)){
            String _tag=process.currentServerTag();
            if(StringUtils.isNotBlank(_tag)){
                process.setTag(_tag);
            }
        }
        return null;
    }
}
