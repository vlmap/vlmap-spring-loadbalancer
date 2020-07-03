package com.github.vlmap.spring.loadbalancer.core;



import java.util.LinkedHashSet;

import java.util.Set;

public class GrayMetedata {

    private LinkedHashSet<String> tags = null;



    public Set<String> getTags() {
        return tags;
    }

    public void setTags(LinkedHashSet<String> tags) {
        this.tags = tags;
    }




}
