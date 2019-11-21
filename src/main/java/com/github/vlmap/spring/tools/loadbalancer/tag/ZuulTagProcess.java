package com.github.vlmap.spring.tools.loadbalancer.tag;


public class ZuulTagProcess extends AbstractTagProcess {

    public void setTag(String tag) {

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
