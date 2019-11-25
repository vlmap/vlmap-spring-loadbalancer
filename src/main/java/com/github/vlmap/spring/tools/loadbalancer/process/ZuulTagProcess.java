package com.github.vlmap.spring.tools.loadbalancer.process;


import com.netflix.zuul.context.RequestContext;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.annotation.Order;

import javax.servlet.http.HttpServletRequest;
@Order(10)
public class ZuulTagProcess extends AbstractTagProcess {

    public void setTag(String tag) {
        if(StringUtils.isBlank(tag))return;
        RequestContext context = RequestContext.getCurrentContext();
        HttpServletRequest request= context.getRequest();
        if (request != null) {
            context.addZuulRequestHeader(this.properties.getTagHeaderName(), tag);

        }

    }

    @Override
    public String getRequestTag() {
        RequestContext context = RequestContext.getCurrentContext();
        HttpServletRequest request= context.getRequest();
        if(request!=null){
            return request.getHeader(this.properties.getTagHeaderName());
        }



        return null;
    }
}
