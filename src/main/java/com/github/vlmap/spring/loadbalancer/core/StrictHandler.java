package com.github.vlmap.spring.loadbalancer.core;

import com.github.vlmap.spring.loadbalancer.GrayLoadBalancerProperties;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.util.AntPathMatcher;

import java.util.Collection;

/**
 * 严格模式校验器
 */
public class StrictHandler {
    CurrentServer clientServer;
    GrayLoadBalancerProperties properties;
    private final AntPathMatcher matcher = new AntPathMatcher();

    public StrictHandler(GrayLoadBalancerProperties properties, CurrentServer clientServer) {
        this.clientServer = clientServer;
        this.properties = properties;
    }

    public boolean validate(String uri, String tag) {
        GrayLoadBalancerProperties.Strict strict = properties.getStrict();

        if (!strict.isEnabled()) {
            return true;
        }
        boolean ignore = false;

        if (strict.getIgnore().getDefault().isEnabled()) {
            ignore = matcher(GrayLoadBalancerProperties.StrictDefaultIgnore.DEFAULT_IGNORE_PATH.get(), uri);
        }
        if (ignore) {
            return true;
        }

        ignore = matcher(strict.getIgnore().getPath(), uri);

        if (ignore) {
            return true;
        }
        if (StringUtils.isBlank(tag)) {

            return !clientServer.isGrayServer();


        }
        return clientServer.container(tag);


    }

    public Collection<String> getGrayTags() {
        return clientServer.getGrayTags();
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

}
