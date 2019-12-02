package com.github.vlmap.spring.tools.loadbalancer;

import java.util.List;
import java.util.Set;

public class Ribbon {

    List<TagOfServers> tagOfServers;

    public List<TagOfServers> getTagOfServers() {
        return tagOfServers;
    }

    public void setTagOfServers(List<TagOfServers> tagOfServers) {
        this.tagOfServers = tagOfServers;
    }

    public static class TagOfServers {
        private   String id;
        private   Set<String> tags;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public Set<String> getTags() {
            return tags;
        }

        public void setTags(Set<String> tags) {
            this.tags = tags;
        }
    }

}
