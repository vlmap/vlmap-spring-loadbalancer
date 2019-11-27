package com.github.vlmap.spring.tools.loadbalancer.process;


import com.github.vlmap.spring.tools.DynamicToolProperties;
import com.netflix.zuul.context.RequestContext;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.annotation.Order;

import javax.servlet.http.HttpServletRequest;
@Order(10)
public class ZuulTagProcess extends AbstractTagProcess {

    public ZuulTagProcess(DynamicToolProperties properties) {
        super(properties);
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
