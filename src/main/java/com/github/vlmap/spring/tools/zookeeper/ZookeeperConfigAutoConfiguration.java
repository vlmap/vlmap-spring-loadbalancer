/*
 * Copyright 2015-2019 the original author or authors.
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

package com.github.vlmap.spring.tools.zookeeper;

import com.github.vlmap.spring.tools.SpringToolsProperties;
import com.github.vlmap.spring.tools.DynamicToolProperties;
import com.github.vlmap.spring.tools.zookeeper.listener.AttachTreeCacheListener;
import com.github.vlmap.spring.tools.zookeeper.listener.ConfigTreeCacheListener;
import org.apache.commons.lang3.StringUtils;
import org.apache.curator.framework.CuratorFramework;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.endpoint.RefreshEndpoint;
import org.springframework.cloud.zookeeper.ConditionalOnZookeeperEnabled;
import org.springframework.cloud.zookeeper.config.ZookeeperConfigProperties;
import org.springframework.cloud.zookeeper.config.ZookeeperPropertySourceLocator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import java.util.List;


/**
 * {@link org.springframework.boot.autoconfigure.EnableAutoConfiguration
 * Auto-configuration} that registers a Zookeeper configuration watcher.
 *
 * @author Spencer Gibb
 * @since 1.0.0
 */
@Configuration
@ConditionalOnZookeeperEnabled
@ConditionalOnProperty(value = "spring.cloud.zookeeper.config.enabled", matchIfMissing = true)
@EnableConfigurationProperties({SpringToolsProperties.class})
public class ZookeeperConfigAutoConfiguration {

     public final static String ATTACH = "spring.cloud.zookeeper.config.attach";

    @Bean
    @ConditionalOnMissingBean

    public DynamicToolProperties dynamicToolProperties(Environment env, SpringToolsProperties properties){
        return new DynamicToolProperties(env,properties);
    }


    @Configuration
    @ConditionalOnClass(RefreshEndpoint.class)
    protected static class ZkRefreshConfiguration {

        @Bean
        @ConditionalOnProperty(name = "spring.cloud.zookeeper.config.watcher.enabled", matchIfMissing = true, havingValue = "false")
        public ConfigWatcher configWatcher(ZookeeperConfigProperties properties, ZookeeperPropertySourceLocator locator, CuratorFramework curator) {


            return new ConfigWatcher(locator.getContexts(), curator, ConfigTreeCacheListener::new);
        }


    }



    /**
     * 实时更新,并且不产生事件RefreshEvent
     *
     * @param env
     * @param curator
     * @param properties
     * @param dynamicToolProperties
     * @return
     */
    @Bean
    public ConfigWatcher configAttachWatcher(Environment env,
                                             CuratorFramework curator,
                                             ZookeeperConfigProperties properties,
                                             DynamicToolProperties dynamicToolProperties) {
        ZookeeperConfigProperties readyProperties = new ZookeeperConfigProperties();
        readyProperties.setEnabled(properties.isEnabled());
        readyProperties.setFailFast(properties.isFailFast());
        readyProperties.setProfileSeparator(properties.getProfileSeparator());

        readyProperties.setDefaultContext(properties.getDefaultContext());
        String root = env.getProperty(ATTACH, properties.getRoot() + "-" + "attach");

        List<String> contexts = null;
        if (!StringUtils.equals(root, properties.getRoot())) {
            readyProperties.setRoot(root);
            ZookeeperPropertySourceLocator locator = new ZookeeperPropertySourceLocator(curator, readyProperties);
            locator.locate(env);
            contexts = locator.getContexts();
//            composite.getPropertySources().clear();
//            for (String context : contexts) {
//
//                PropertySource propertySource = new MapPropertySource(context, new ProxyMap(new HashMap<>(), true));
//
//                composite.addPropertySource(propertySource);
//
//
//            }

        }


        ConfigWatcher watcher = new ConfigWatcher(contexts, curator, AttachTreeCacheListener::new);
        watcher.setPropertySource(dynamicToolProperties.getPropertySource());


//        String defaultContext = root + "/" + properties.getDefaultContext();
//        writer.setDefaultContext(defaultContext);
//        writer.setContext(contexts);
//        writer.setCurator(curator);
        return watcher;
    }

//    ConfigAttachWriter writer = new ConfigAttachWriter();
//
//    @Bean
//    public ConfigAttachWriter ConfigAttachWriter(@Autowired @Qualifier("configAttachWatcher") ConfigWatcher configWatcher) {
//        return writer;
//    }

//    @Configuration
//    @AutoConfigureAfter(EurekaClientAutoConfiguration.class)
//    protected static class ZkEventListenerConfiguration {
//        @Bean
//        @ConditionalOnClass(EurekaInstanceConfigBean.class)
//        public ZkAttachValue eurekaEventListener() {
//            return new ZkAttachValue();
//        }
//    }


}
