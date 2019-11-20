package com.github.vlmap.cloud.loadbalancer;

import com.github.vlmap.cloud.loadbalancer.tag.TagProcess;
import com.netflix.client.config.IClientConfig;
import com.netflix.loadbalancer.*;
 import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
 import java.util.concurrent.ThreadLocalRandom;


/**
 * A loadbalacing strategy that randomly distributes traffic amongst existing
 * servers.
 *
 * @author stonse
 */
public class TagRule extends AbstractLoadBalancerRule {





    public TagRule(ILoadBalancer lb) {

        setLoadBalancer(lb);
    }


    @Autowired
    private Map<String, TagProcess> tagProcessMap;

    protected String tag() {
        for (TagProcess process : tagProcessMap.values()) {
            String tag = process.getTag();
            if (StringUtils.isNotBlank(tag)) {
                return tag;
            }
        }
        return null;
    }

    protected void setTag(String tag) {
        for (TagProcess process : tagProcessMap.values()) {
            process.setTag(tag);

        }

    }

    protected String getServerTag(Server server) {
        return null;
    }




    @Override
    public Server choose(Object key) {
        String tag = tag();
        ILoadBalancer lb = getLoadBalancer();
        List<Server> allServers = lb.getAllServers();

        List<Server> normalAllServers = new ArrayList<>(allServers.size());

        if (StringUtils.isNotBlank(tag)) {
            setTag(tag);


            for (Server server : allServers) {
                if (server.isAlive() && (server.isReadyToServe())) {
                    String serverTag = getServerTag(server);
                    if (StringUtils.isNotBlank(serverTag)) {
                        normalAllServers.add(server);
                    } else if (StringUtils.equals(serverTag, tag)) {
                        return server;
                    }
                }


            }

        }
        if (!normalAllServers.isEmpty()) {
            int index = ThreadLocalRandom.current().nextInt(normalAllServers.size());
            return normalAllServers.get(index);
        }

        return null;
    }

    @Override
    public void initWithNiwsConfig(IClientConfig clientConfig) {
    }
}
