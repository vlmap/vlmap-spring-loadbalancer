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

import com.github.vlmap.spring.tools.SpringToolsAutoConfiguration;
import com.github.vlmap.spring.tools.SpringToolsProperties;
import com.github.vlmap.spring.tools.zookeeper.listener.PropTreeCacheListener;
import org.apache.curator.framework.CuratorFramework;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.zookeeper.ConditionalOnZookeeperEnabled;
import org.springframework.cloud.zookeeper.ZookeeperProperties;
import org.springframework.cloud.zookeeper.config.ZookeeperPropertySourceLocator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.core.env.Environment;

import java.util.List;


/**
 * 停用zookeeper 默认watcher时才启用
 */
@Configuration
@ConditionalOnZookeeperEnabled
@ConditionalOnProperty(name = "spring.cloud.zookeeper.config.watcher.enabled", havingValue = "false", matchIfMissing = true)
@EnableConfigurationProperties({ZookeeperProperties.class, SpringToolsProperties.class})

public class ZookeeperPropertiesAutoConfiguration {


    @Bean
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public PropTreeCacheListener propsTreeCacheListener(Environment environment, SpringToolsProperties properties) {
        PropTreeCacheListener listener = new PropTreeCacheListener(environment, properties);
        return listener;
    }


    @Bean
    public PropWatcher propWatcher(CuratorFramework curator, ZookeeperPropertySourceLocator locator, ObjectProvider<PropTreeCacheListener> provider) {


        List<String> contexts = locator.getContexts();
        PropWatcher watcher = new PropWatcher(contexts, curator, provider);

        return watcher;
    }


}
