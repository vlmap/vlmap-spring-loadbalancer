package com.github.vlmap.spring.loadbalancer.core.platform.servlet;

import com.github.vlmap.spring.loadbalancer.GrayLoadBalancerProperties;
import com.github.vlmap.spring.loadbalancer.core.platform.ResponderFilter;
import com.github.vlmap.spring.loadbalancer.core.platform.ResponderParamater;
import com.github.vlmap.spring.loadbalancer.util.Util;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.MediaType;
import org.springframework.util.MultiValueMap;

import javax.servlet.*;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;


public class ResponderServletFilter extends ResponderFilter implements Filter {


    public ResponderServletFilter(GrayLoadBalancerProperties properties) {

        super(properties);

    }
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void destroy() {

    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        HttpServletResponse httpServletResponse = (HttpServletResponse) response;

        String headerName = properties.getHeaderName();
        String tag = httpServletRequest.getHeader(headerName);


        if (Util.isEnabled(this.properties.getResponder())  && StringUtils.isNotBlank(tag)) {

            ResponderParamater data = getParamater(this.paramaters, tag);
            if (data != null) {
                if (logger.isTraceEnabled()) {
                    logger.trace("Apply Responder:" + data.toString());
                }
                httpServletResponse.setStatus(data.getCode());
                MultiValueMap<String, String> cookies = data.getCookies();
                if (cookies != null) {

                    for (Map.Entry<String, List<String>> entry : cookies.entrySet()) {
                        String name = entry.getKey();
                        List<String> values = entry.getValue();
                        for (String value : values) {
                            httpServletResponse.addCookie(new Cookie(name, value));
                        }
                    }


                }
                MultiValueMap<String, String> headers = data.getHeaders();
                if (headers != null) {

                    for (Map.Entry<String, List<String>> entry : headers.entrySet()) {
                        String name = entry.getKey();
                        List<String> values = entry.getValue();
                        for (String value : values) {
                            httpServletResponse.addHeader(name, value);
                        }
                    }


                }

                String body = data.getBody();
                if (body != null) {
                    String charsetName = httpServletResponse.getCharacterEncoding();


                    Charset charset = StringUtils.isBlank(charsetName) ? Charset.forName(charsetName) : StandardCharsets.UTF_8;

                    String contentType = response.getContentType();
                    if (contentType == null) {
                        response.setContentType(MediaType.TEXT_PLAIN_VALUE + ";charset=" + charset.name());
                    }
                    byte[] bytes = body.getBytes(charset);
                    response.setContentLength(bytes.length);
                    ServletOutputStream outputStream = httpServletResponse.getOutputStream();
                    outputStream.write(bytes);
                    outputStream.close();

                }
                return;

            }


        }
        chain.doFilter(request, response);

    }


}
