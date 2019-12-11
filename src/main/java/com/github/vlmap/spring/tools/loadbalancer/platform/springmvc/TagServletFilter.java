package com.github.vlmap.spring.tools.loadbalancer.platform.springmvc;

import com.github.vlmap.spring.tools.SpringToolsProperties;
import com.github.vlmap.spring.tools.common.AntPathMatcherUtils;
import com.github.vlmap.spring.tools.context.ContextManager;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.EnumerationUtils;
import org.apache.commons.collections.iterators.IteratorEnumeration;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.servlet.filter.OrderedFilter;
import org.springframework.core.Ordered;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;


public class TagServletFilter implements OrderedFilter {
    Logger logger= LoggerFactory.getLogger(this.getClass());

    SpringToolsProperties properties;

    public TagServletFilter(SpringToolsProperties properties) {
        this.properties = properties;
    }


    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        HttpServletResponse httpServletResponse=(HttpServletResponse)response;
        String name = this.properties.getTagHeaderName();
        String tag = httpServletRequest.getHeader(name);
       String serverTag= properties.getTagLoadbalancer().getHeader();
        /**
         * 非兼容模式,请求标签不匹配拒绝响应
         */
         SpringToolsProperties.Compatible compatible=properties.getCompatible();
        if(! compatible.isEnabled()&&StringUtils.isNotBlank(serverTag)&&!StringUtils.equals(tag,serverTag)){
            List<String> ignoreUrls=compatible.ignoreUrls();
            String uri = ((HttpServletRequest) request).getRequestURI();

            if(!AntPathMatcherUtils.matcher(ignoreUrls,uri)) {
                if (logger.isInfoEnabled()) {
                    logger.info("The server isn't compatible model,current request Header[" + name + ":" + tag + "] don't match \"" + serverTag + "\",response code:" + compatible.getCode());

                }
                String message = compatible.getMessage();
                if (StringUtils.isBlank(message)) {
                    httpServletResponse.setStatus(compatible.getCode());

                } else {
                    httpServletResponse.sendError(compatible.getCode(), message);
                }
                return;
            }
        }
        try {
            if (StringUtils.isBlank(tag)) {

                tag = properties.getTagLoadbalancer().getHeader();
                if (StringUtils.isNotBlank(tag)) {

                    Map<String, List<String>> headers = getHeaders(httpServletRequest);

                    addHeader(headers, name, tag);

                    request    = new HttpServletRequestWrapper(httpServletRequest, headers);



                }
            }

            ContextManager.getRuntimeContext().setTag(tag);
            chain.doFilter(request, response);

        } finally {
            ContextManager.getRuntimeContext().onComplete();
        }

    }

    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }

    protected void addHeader(Map<String, List<String>> headers, String name, String value) {

        List<String> values = headers.get(name);
        if (values == null) {
            values = new ArrayList<>();
            headers.put(name, values);

        }
        values.add(value);
    }

    protected Map<String, List<String>> getHeaders(HttpServletRequest httpServletRequest) {

        Enumeration<String> enumeration = httpServletRequest.getHeaderNames();
        Map<String, List<String>> headers = new HashMap<>();
        while (enumeration.hasMoreElements()) {
            String headerName = enumeration.nextElement();
            headers.put(headerName, EnumerationUtils.toList(httpServletRequest.getHeaders(headerName)));
        }
        return headers;

    }

    private static class HttpServletRequestWrapper extends javax.servlet.http.HttpServletRequestWrapper {
        Map<String, List<String>> headers;

        public HttpServletRequestWrapper(HttpServletRequest request, Map<String, List<String>> headers) {
            super(request);
            this.headers = headers;
        }

        @Override
        public Enumeration<String> getHeaderNames() {


            return new IteratorEnumeration(headers.keySet().iterator());
        }

        @Override
        public Enumeration<String> getHeaders(String name) {
            List<String> headerValues = headers.get(name);
            headerValues = headerValues == null ? Collections.emptyList() : headerValues;

            return new IteratorEnumeration(headerValues.iterator());
        }

        @Override
        public String getHeader(String name) {
            List<String> headerValues = headers.get(name);
            if (CollectionUtils.isEmpty(headerValues)) {
                return null;
            }
            return headerValues.get(0);
        }
    }
}
