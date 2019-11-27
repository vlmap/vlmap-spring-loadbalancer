package com.github.vlmap.spring.tools.loadbalancer.process;


import com.github.vlmap.spring.tools.DynamicToolProperties;
import org.springframework.core.annotation.Order;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
@Order(30)
public class SpringMvcTagProcess extends AbstractTagProcess {

    public SpringMvcTagProcess(DynamicToolProperties properties) {
        super(properties);
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
