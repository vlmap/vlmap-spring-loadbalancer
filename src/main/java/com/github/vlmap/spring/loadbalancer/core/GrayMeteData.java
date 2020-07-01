package com.github.vlmap.spring.loadbalancer.core;

import com.github.vlmap.spring.loadbalancer.GrayLoadBalancerProperties;

import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

public class GrayMeteData {

    private GrayLoadBalancerProperties.Enabled strict=new GrayLoadBalancerProperties.Enabled(false);
    private LinkedHashSet<String> tags = null;



    public Set<String> getTags() {
        return tags;
    }

    public void setTags(LinkedHashSet<String> tags) {
        this.tags = tags;
    }

    public GrayLoadBalancerProperties.Enabled  getStrict() {
        return strict;
    }

    public void setStrict(GrayLoadBalancerProperties.Enabled  strict) {
        this.strict = strict;
    }


}
