package com.github.vlmap.spring.tools.loadbalancer.platform;

import com.github.vlmap.spring.tools.GrayLoadBalancerProperties;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.AntPathMatcher;

import java.util.Collection;

public interface IStrictHandler {

    default boolean should(GrayLoadBalancerProperties properties, String tag) {
        String serverTag = properties.getHeader();
        GrayLoadBalancerProperties.Strict strict = properties.getStrict();
        if(StringUtils.equals(tag, serverTag)){
           return false;
        }
        if(Platform.getInstnce().isGatewayService()){
            return false;
        }
        return strict.isEnabled();

    }

    /**
     * 再不兼容的情况下，判断是否需要兼容当前请求，即忽略
     * @param properties
     * @param uri
     * @return
     */
    default boolean shouldIgnore(GrayLoadBalancerProperties properties, String uri) {
        GrayLoadBalancerProperties.Strict strict = properties.getStrict();

        boolean ignore = PathMatcher.matcher(strict.getIgnore().getPath(), uri);
        if (!ignore) {
            ignore = PathMatcher.matcher(GrayLoadBalancerProperties.CompatibleIgnore.DEFAULT_IGNORE_PATH.get(), uri);
        }
        return ignore;
    }

    final class PathMatcher {
        static AntPathMatcher matcher = new AntPathMatcher();

        private static boolean matcher(Collection<String> list, String uri) {
            if (CollectionUtils.isNotEmpty(list)) {

                for (String ignoreUrl : list) {
                    if (matcher.match(ignoreUrl, uri)) {
                        return true;

                    }
                }

            }
            return false;
        }
    }

}
