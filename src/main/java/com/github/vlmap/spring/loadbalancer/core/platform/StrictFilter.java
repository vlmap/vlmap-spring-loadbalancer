package com.github.vlmap.spring.loadbalancer.core.platform;

import com.github.vlmap.spring.loadbalancer.GrayLoadBalancerProperties;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.core.Ordered;
import org.springframework.core.env.Environment;
import org.springframework.util.AntPathMatcher;

import java.util.*;

public class StrictFilter implements Ordered {
    protected Logger logger = LoggerFactory.getLogger(this.getClass());

    protected GrayLoadBalancerProperties properties;

    private final AntPathMatcher matcher = new AntPathMatcher();
    private List<String> ignores = Collections.emptyList();
    @Autowired(required = false)
    private ServiceInstance serviceInstance;

    public StrictFilter(GrayLoadBalancerProperties properties) {
        this.properties = properties;
    }

    @Autowired
    public void initMethod(Environment environment) {
        List<String> patterns = new ArrayList<>();
        patterns.add("/webjars/**");
        patterns.add("/favicon.ico");
        String path = environment.getProperty("management.endpoints.web.base-path", "/actuator");
        patterns.add(pattern(path));
        this.ignores = Collections.unmodifiableList(patterns);
    }

    private String pattern(String uri) {

        if (StringUtils.isBlank(uri)) {
            return null;
        }
        if (StringUtils.endsWith(uri, "/**")) {
            return uri;
        }
        if (uri.endsWith("/")) {
            return uri + "**";
        }
        return uri + "/**";
    }

    /**
     * 正常请求负载到灰度节点或灰度请求负载到非灰度节点验证不通过
     *
     * @param uri
     * @param tag 当前请求的灰度值
     * @return
     */
    public boolean validate(String uri, String tag) {
        GrayLoadBalancerProperties.Strict strict = properties.getStrict();

        if (isIgnore(strict, uri)) {
            return true;
        }
        boolean isGrayServer = isGrayServer();
        boolean isGrayRequest = StringUtils.isNotBlank(tag);
        return isGrayServer == isGrayRequest;


    }

    protected boolean isGrayServer() {
        String tags = getGrayTags();
        if (StringUtils.isNotBlank(tags)) {
            return true;
        }
        return false;
    }

    private boolean isIgnore(GrayLoadBalancerProperties.Strict strict, String uri) {
        boolean ignore = false;

        if (strict.getIgnore().getDefault().isEnabled()) {
            ignore = matcher(ignores, uri);
        }
        if (ignore) {
            return true;
        }

        ignore = matcher(strict.getIgnore().getPath(), uri);


        return ignore;
    }

    public String getGrayTags() {
        if (this.serviceInstance != null) {
            Map<String, String> metadata = serviceInstance.getMetadata();
            if (metadata != null) {
                String tags = metadata.get("gray.tags");
                return tags;
            }
        }
        return StringUtils.EMPTY;
    }

    private boolean matcher(Collection<String> list, String uri) {
        if (CollectionUtils.isNotEmpty(list)) {

            for (String ignoreUrl : list) {
                if (matcher.match(ignoreUrl, uri)) {
                    return true;

                }
            }

        }
        return false;
    }

    public int getCode() {
        GrayLoadBalancerProperties.Strict strict = properties.getStrict();


        return strict.getCode();
    }


    public String getMessage() {
        GrayLoadBalancerProperties.Strict strict = properties.getStrict();

        return strict.getMessage();
    }

    @Override
    public int getOrder() {
        return FilterOrder.ORDER_STRICT_FILTER;
    }
}
