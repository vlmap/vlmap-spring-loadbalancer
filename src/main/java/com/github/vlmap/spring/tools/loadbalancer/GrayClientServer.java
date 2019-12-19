package com.github.vlmap.spring.tools.loadbalancer;

import com.github.vlmap.spring.tools.context.GrayUtils;

import javax.annotation.PostConstruct;
import java.util.*;

public class GrayClientServer {
    private String clientName;
    Map<String, Set<String>> clientServerTags;

    public Map<String, Set<String>> getClientServerTags() {
        return clientServerTags;
    }

    public GrayClientServer(String clientName) {
        this.clientName = clientName;
    }

    @PostConstruct
    public void initMethod() {
        this.clientServerTags = GrayUtils.tagOfServer(clientName);




    }



}
