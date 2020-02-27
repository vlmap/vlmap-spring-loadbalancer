package com.github.vlmap.spring.loadbalancer.util;


import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.boot.context.properties.source.ConfigurationPropertySource;
import org.springframework.core.env.ConfigurableEnvironment;

import java.util.*;

public class GrayUtils {


    public static Map<String, Set<String>> tagOfServer(ConfigurableEnvironment environment, String clientName) {
        clientName = StringUtils.upperCase(clientName);

        ConfigurationPropertySource propertySource = EnvironmentUtils.getSubsetConfigurationPropertySource(environment, clientName);


        Binder binder = new Binder(propertySource);
        GrayTagOfServersProperties ribbon = new GrayTagOfServersProperties();
        binder.bind("ribbon", Bindable.ofInstance(ribbon));

        List<TagOfServers> tagOfServers = ribbon.getGray();
        if (tagOfServers != null) {
            Map<String, Set<String>> map = new HashMap<>(tagOfServers.size());

            for (TagOfServers tagOfServer : tagOfServers) {

                if (tagOfServer != null && CollectionUtils.isNotEmpty(tagOfServer.getTags()) && StringUtils.isNotBlank(tagOfServer.getId())) {
                    Pair<String, Integer> hostPort = getHostPort(tagOfServer.getId());
                    if (hostPort != null) {
                        String id = hostPort.first() + ":" + hostPort.second();

                        map.put(id, tagOfServer.getTags());
                    }

                }
            }
            return Collections.unmodifiableMap(map);


        }
        return Collections.emptyMap();
    }

    public static Pair<String, Integer> getHostPort(String id) {
        if (id != null) {
            String host = null;
            int port = 80;

            if (id.toLowerCase().startsWith("http://")) {
                id = id.substring(7);
                port = 80;
            } else if (id.toLowerCase().startsWith("https://")) {
                id = id.substring(8);
                port = 443;
            }

            if (id.contains("/")) {
                int slash_idx = id.indexOf("/");
                id = id.substring(0, slash_idx);
            }

            int colon_idx = id.indexOf(':');

            if (colon_idx == -1) {
                host = id; // default
            } else {
                host = id.substring(0, colon_idx);
                try {
                    port = Integer.parseInt(id.substring(colon_idx + 1));
                } catch (NumberFormatException e) {
                    throw e;
                }
            }
            return new Pair<String, Integer>(host, port);
        } else {
            return null;
        }

    }


}
