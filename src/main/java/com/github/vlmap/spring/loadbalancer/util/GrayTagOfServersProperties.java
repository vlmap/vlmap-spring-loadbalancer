package com.github.vlmap.spring.loadbalancer.util;

import java.util.List;


public class GrayTagOfServersProperties {


    List<TagOfServers> gray;

    public List<TagOfServers> getGray() {
        return gray;
    }

    public void setGray(List<TagOfServers> gray) {
        this.gray = gray;
    }
}
