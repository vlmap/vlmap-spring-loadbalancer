package com.github.vlmap.spring.tools.loadbalancer.process;


 import com.github.vlmap.spring.tools.SpringToolsProperties;
import org.apache.commons.lang.StringUtils;
import org.springframework.core.annotation.Order;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
@Order(30)
public class SpringmvcTagProcess extends AbstractTagProcess {

    public SpringmvcTagProcess(SpringToolsProperties properties) {
        super(properties);
    }

    @Override
    public String getTag() {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        String header=null;
        if(request!=null){
            header= request.getHeader(this.properties.getTagHeaderName());
        }

        if(StringUtils.isBlank(header)){
            header=properties.getTagLoadbalancer().getHeader();
        }
        return header;
    }
}
