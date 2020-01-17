package com.github.vlmap.spring.loadbalancer.core.platform.servlet;

import com.github.vlmap.spring.loadbalancer.GrayLoadBalancerProperties;
import com.github.vlmap.spring.loadbalancer.core.attach.AttachHandler;
import com.github.vlmap.spring.loadbalancer.core.attach.ServletAttachHandler;
import com.github.vlmap.spring.loadbalancer.core.attach.cli.GaryAttachParamater;
import com.github.vlmap.spring.loadbalancer.core.platform.FilterOrder;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.EnumerationUtils;
import org.apache.commons.collections.IteratorUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.servlet.filter.OrderedFilter;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.ContentCachingRequestWrapper;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;


public class GrayAttachServletFilter implements OrderedFilter {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private GrayLoadBalancerProperties properties;


    private ServletAttachHandler attachHandler;

    public GrayAttachServletFilter(GrayLoadBalancerProperties properties, ServletAttachHandler attachHandler) {

        this.properties = properties;
        this.attachHandler = attachHandler;

    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

        if(!this.properties.getAttach().isEnabled()){
            chain.doFilter(request,response);
            return ;
        }
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;

        List<GaryAttachParamater> paramaters = attachHandler.getAttachParamaters();
        AttachHandler.SimpleRequestData data = new AttachHandler.SimpleRequestData();


        if (CollectionUtils.isNotEmpty(paramaters)) {
            List<String> headers = new ArrayList<>();
            MediaType contentType = MediaType.valueOf(request.getContentType());
            HttpMethod method = HttpMethod.resolve(httpServletRequest.getMethod());

            if (attachHandler.isReadBody(paramaters, contentType, method)) {
                httpServletRequest = new ContentCachingRequestWrapper(httpServletRequest);
            }


            paramaters(httpServletRequest, data, paramaters);
            attachHandler.match(data, paramaters, headers);
            if (CollectionUtils.isNotEmpty(headers)) {

                MultiValueMap<String, String> addHeader = new LinkedMultiValueMap<>();
                addHeader.put(properties.getHeaderName(), headers);
                httpServletRequest = addHeader(httpServletRequest, addHeader);


            }
            chain.doFilter(httpServletRequest, response);


        }


    }

    protected HttpServletRequest addHeader(HttpServletRequest httpServletRequest, MultiValueMap<String, String> addHeader) {
        Enumeration<String> headerNames = httpServletRequest.getHeaderNames();
        MultiValueMap<String, String> headers = new LinkedMultiValueMap<>();

        while (headerNames.hasMoreElements()) {
            String headerName = headerNames.nextElement();
            List<String> list = EnumerationUtils.toList(httpServletRequest.getHeaders(headerName));
            headers.put(headerName, list);
        }
        headers.addAll(addHeader);
        return new HttpServletRequestWrapper(httpServletRequest) {
            @Override
            public String getHeader(String name) {
                return headers.getFirst(name);
            }

            @Override
            public Enumeration<String> getHeaderNames() {
                return IteratorUtils.asEnumeration(headers.keySet().iterator());
            }

            @Override
            public Enumeration<String> getHeaders(String name) {
                List<String> list = headers.getOrDefault(name, Collections.emptyList());
                return IteratorUtils.asEnumeration(list.iterator());

            }
        };
    }

    /**
     * 收集参数
     *
     * @param
     * @param paramaters
     * @return
     */
    protected AttachHandler.SimpleRequestData paramaters(HttpServletRequest httpServletRequest, AttachHandler.SimpleRequestData data, List<GaryAttachParamater> paramaters) {
        return attachHandler.parser(paramaters, data, httpServletRequest);
    }


    public int getOrder() {
        return FilterOrder.ORDER_ATTACH_FILTER;
    }

}