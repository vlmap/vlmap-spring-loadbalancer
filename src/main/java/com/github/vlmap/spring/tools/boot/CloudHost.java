/*
 * Copyright 2012-2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.vlmap.spring.tools.boot;

import com.github.vlmap.spring.tools.SpringApplicationPatch;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.*;
import java.util.stream.Collectors;
//@formatter:off

/**
 * host:
 * instance:
 * - 10.206.2.253
 * - 10.206.2.238
 * - 172.18.70.27
 * discover:
 * - ${host.instance[0]}:8761
 * - ${host.instance[1]}:8761
 * 云环境配置信息，
 */
//@formatter:on
//@ConfigurationProperties("host")
public class CloudHost {
    private static Logger logger = LoggerFactory.getLogger(SpringApplicationPatch.class);


    /**
     * 微服务所有机器地址（HOST，或IP）
     */
    private Set<String> instance;
    /**
     * 微服务中注册发现服务，地址口和端（例：192.168.1.1:8761）
     */
    private Set<String> discover;


    public void setInstance(Set<String> instance) {
        this.instance = instance;
    }


    public void setDiscover(Set<String> discover) {
        this.discover = discover;
    }

    /**
     * 上报到注册发现的地址
     *
     * @return
     */
    public String getEurekaInstanceIpAddress() {
        InetAddress instanceAddress = matchLocalHost(instance);
        String eurekaIpAddress = null;
        if (instanceAddress != null) {
            eurekaIpAddress = instanceAddress.getHostAddress();
        }
        return eurekaIpAddress;
    }

    /**
     * 注册发现地址
     *
     * @return
     */
    public String getEurekaServiceUrl() {
        if (CollectionUtils.isNotEmpty(discover)) {
            return StringUtils.join(discover.stream().map(it -> {
                if (StringUtils.startsWith(it, "http://") || StringUtils.startsWith(it, "https://")) {
                    return String.format("%s/eureka/", it);
                } else {
                    return String.format("http://%s/eureka/", it);
                }
            }).toArray(), ",");
        }
        return null;
    }

    protected InetAddress matchLocalHost(Collection<String> hosts) {

        List<InetAddress> hostList = null;
        if (CollectionUtils.isNotEmpty(hosts)) {
            hostList = hosts.stream().map(it -> {
                try {
                    return InetAddress.getByName(it);
                } catch (Exception e) {

                }
                return null;

            }).filter(it -> it != null).collect(Collectors.toList());
        }


        List<InetAddress> localList = new ArrayList<>();

        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
                NetworkInterface intf = en.nextElement();
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                    InetAddress inetAddress = enumIpAddr.nextElement();
                    localList.add(inetAddress);

                }
            }
        } catch (Exception e) {
            logger.warn("", e);
        }

        if (CollectionUtils.isNotEmpty(hostList)) {
            for (InetAddress host : hostList) {
                for (InetAddress local : localList) {
                    if (StringUtils.equalsIgnoreCase(host.getHostAddress(), local.getHostAddress())) {
                        return host;
                    }
                }
            }
        }


        return null;
    }
}
