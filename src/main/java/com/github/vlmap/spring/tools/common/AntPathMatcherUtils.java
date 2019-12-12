package com.github.vlmap.spring.tools.common;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.AntPathMatcher;

import java.util.Collection;

public class AntPathMatcherUtils {
    static AntPathMatcher matcher = new AntPathMatcher();

    public static String toAntPath(String uri) {

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

    public static boolean matcher(Collection<String> antpaths, String uri) {
        if (CollectionUtils.isNotEmpty(antpaths)) {

            for (String ignoreUrl : antpaths) {
                if (matcher.match(ignoreUrl, uri)) {
                    return true;

                }
            }

        }
        return false;
    }
}
