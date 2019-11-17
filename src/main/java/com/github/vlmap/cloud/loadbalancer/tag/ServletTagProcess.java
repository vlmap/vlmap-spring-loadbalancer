package com.github.vlmap.cloud.loadbalancer.tag;


 import com.github.vlmap.cloud.loadbalancer.tag.AbstractTagProcess;
 import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;


public class ServletTagProcess  extends AbstractTagProcess {

    public void setTag(String tag){

//        ServerHttpRequest request= TagContextHolder.getRequest();
//        if(request!=null){
//            request.getHeaders().add(LOADBALANCER_TAG_HEADER,tag);
//        }
    }

    @Override
    protected String getRequestTag() {
//        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
//        if (request != null) {
//            String tag = request.getHeader(LOADBALANCER_TAG_HEADER);
//           return tag;
//        }

        return null;
    }
}
