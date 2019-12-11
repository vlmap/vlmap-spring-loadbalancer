package com.github.vlmap.spring.tools.loadbalancer.platform.springmvc;

import com.github.vlmap.spring.tools.SpringToolsProperties;
import com.github.vlmap.spring.tools.common.AntPathMatcherUtils;
import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.util.List;

import static org.springframework.cloud.netflix.zuul.filters.support.FilterConstants.PRE_TYPE;

public class TagZuulCompatibleFilter extends ZuulFilter {
    Logger logger= LoggerFactory.getLogger(this.getClass());

    SpringToolsProperties properties;

    public TagZuulCompatibleFilter(SpringToolsProperties properties) {
        this.properties = properties;
    }


    @Override
    public String filterType() {
        return PRE_TYPE;
    }

    @Override
    public int filterOrder() {
        return Integer.MIN_VALUE;
    }

    @Override
    public boolean shouldFilter() {
        return !properties.getCompatible().isEnabled();
    }

    @Override
    public Object run() throws ZuulException {
        String name = this.properties.getTagHeaderName();
        RequestContext ctx = RequestContext.getCurrentContext();
        HttpServletResponse httpServletResponse =ctx.getResponse();
        HttpServletRequest httpServletRequest =ctx.getRequest();

        String serverTag= properties.getTagLoadbalancer().getHeader();
         String tag= httpServletRequest.getHeader(name);
        /**
         * 非兼容模式,请求标签不匹配拒绝响应
         */
         SpringToolsProperties.Compatible compatible=properties.getCompatible();
        if(! compatible.isEnabled()&& StringUtils.isNotBlank(serverTag)&&!StringUtils.equals(tag,serverTag)){
            List<String> ignoreUrls=compatible.ignoreUrls();
            String uri = httpServletRequest.getRequestURI();

            if(!AntPathMatcherUtils.matcher(ignoreUrls,uri)) {
                if(logger.isInfoEnabled()){
                    logger.info("The server isn't compatible model,current request Header["+name+":"+tag+"] don't match \""+serverTag+"\",response code:"+compatible.getCode());

                }
                ctx.setSendZuulResponse(false);
                String message=compatible.getMessage();
                if(StringUtils.isBlank(message)){
                    httpServletResponse.setStatus(compatible.getCode());

                }else {
                    try{
                        httpServletResponse.sendError(compatible.getCode(),message);

                    }catch (Exception e){
                        throw new ZuulException("sendError exception ",compatible.getCode(),message);
                    }
                }
            }


        }
        return null;
    }
}
