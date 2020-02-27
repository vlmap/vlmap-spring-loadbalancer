package com.github.vlmap.spring.loadbalancer.core.platform;

import org.apache.commons.lang.ObjectUtils;


public class RequestMatchParamaterComparator implements java.util.Comparator<RequestMatchParamater> {
    java.util.Comparator<String> comparator;

    public RequestMatchParamaterComparator(java.util.Comparator<String> comparator) {
        this.comparator = comparator;
    }

    @Override
    public int compare(RequestMatchParamater o1, RequestMatchParamater o2) {
        return comparator.compare(ObjectUtils.toString(o1.getPath()), ObjectUtils.toString(o2.getPath()));
    }
}