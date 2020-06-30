package com.github.vlmap.spring.loadbalancer.core;

import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

public class GrayInfo {
    Map<String, String> metadata;

    private LinkedHashSet<String> tags = null;

    public Map<String, String> getMetadata() {
        return metadata;
    }

    public void setMetadata(Map<String, String> metadata) {
        this.metadata = metadata;
    }

    public Set<String> getTags() {
        return tags;
    }

    public void setTags(LinkedHashSet<String> tags) {
        this.tags = tags;
    }
}
