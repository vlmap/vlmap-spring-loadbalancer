package com.github.vlmap.cloud.loadbalancer;

import org.springframework.core.env.AbstractEnvironment;
import org.springframework.core.env.PropertySource;

public class AttachEnvironment extends AbstractEnvironment {
    public AttachEnvironment(PropertySource propertySource) {
        super();
        getPropertySources().addFirst(propertySource);
    }
}
