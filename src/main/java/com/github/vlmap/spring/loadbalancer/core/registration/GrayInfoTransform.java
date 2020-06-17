package com.github.vlmap.spring.loadbalancer.core.registration;

import com.github.vlmap.spring.loadbalancer.core.GrayInfo;
import com.netflix.loadbalancer.Server;

public interface GrayInfoTransform {
      GrayInfo transform(Server server);
}
