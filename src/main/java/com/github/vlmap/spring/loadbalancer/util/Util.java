package com.github.vlmap.spring.loadbalancer.util;

import com.github.vlmap.spring.loadbalancer.GrayLoadBalancerProperties;

public class Util {
    public static boolean isEnabled(GrayLoadBalancerProperties.Enabled enabled){
        if(enabled!=null){
            return enabled.isEnabled();
        }
        return false;
    }
}
