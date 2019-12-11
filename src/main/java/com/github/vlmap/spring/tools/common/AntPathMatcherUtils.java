package com.github.vlmap.spring.tools.common;

import org.apache.commons.collections.CollectionUtils;
import org.springframework.util.AntPathMatcher;

import java.util.Collection;

public class AntPathMatcherUtils {
    static     AntPathMatcher matcher=new AntPathMatcher();

    public static  boolean matcher(Collection<String> antpaths, String uri ){
        if(CollectionUtils.isNotEmpty(antpaths)){

            for(String ignoreUrl:antpaths){
                if(matcher.match(ignoreUrl,uri)){
                    return true;

                }
            }

        }
        return false;
    }
}
