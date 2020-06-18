package com.github.vlmap.spring.loadbalancer.core.registration;

import com.github.vlmap.spring.loadbalancer.core.GrayInfo;
import com.netflix.loadbalancer.Server;

import java.util.List;
import java.util.Map;

public interface GrayInfoTransform<T extends Server> {
    Map<T, GrayInfo> transform(List<T> servers);

    GrayInfo transform(T server);

}
