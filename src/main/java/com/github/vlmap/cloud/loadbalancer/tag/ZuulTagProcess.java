package com.github.vlmap.cloud.loadbalancer.tag;


public class ZuulTagProcess extends AbstractTagProcess {

    public void setTag(String tag){

//        ServerHttpRequest request= TagContextHolder.getRequest();
//        if(request!=null){
//            request.getHeaders().add(loadbalancerTag,tag);
//        }
    }

    @Override
    protected String getRequestTag() {
//        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
//        if (request != null) {
//            String tag = request.getHeader(loadbalancerTag);
//           return tag;
//        }

        return null;
    }
}
