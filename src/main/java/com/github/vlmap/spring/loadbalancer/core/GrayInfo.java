package com.github.vlmap.spring.loadbalancer.core;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

public class GrayInfo {
    Map<String, String> metadata;

    private Set<String> tags = Collections.emptySet();

    public Map<String, String> getMetadata() {
        return metadata;
    }

    public void setMetadata(Map<String, String> metadata) {
        this.metadata = metadata;
    }

    public Set<String> getTags() {
        return tags;
    }

    public void setTags(Set<String> tags) {
        this.tags = tags;
    }
}
