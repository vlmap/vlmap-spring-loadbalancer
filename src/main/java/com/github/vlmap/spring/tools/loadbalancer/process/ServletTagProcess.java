package com.github.vlmap.spring.tools.loadbalancer.process;


import org.springframework.core.annotation.Order;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
@Order(30)
public class ServletTagProcess extends AbstractTagProcess {

    public void setTag(String tag) {
      //交由feign resttemplate 的Interceptor处理
    }

    @Override
    public String getRequestTag() {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        if(request!=null){
            return request.getHeader(this.properties.getTagHeaderName());
        }

        return null;
    }
}
