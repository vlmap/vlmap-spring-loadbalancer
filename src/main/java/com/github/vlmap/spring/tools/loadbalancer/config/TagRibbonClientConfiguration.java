package com.github.vlmap.spring.tools.loadbalancer.config;


import com.github.vlmap.spring.tools.SpringToolsAutoConfiguration;
import com.github.vlmap.spring.tools.SpringToolsProperties;
import com.github.vlmap.spring.tools.event.PropertyChangeEvent;
import com.github.vlmap.spring.tools.event.listener.DelegatePropChangeListener;
import com.github.vlmap.spring.tools.event.listener.PropertiesListener;
import com.github.vlmap.spring.tools.loadbalancer.DelegatingLoadBalancer;
import com.github.vlmap.spring.tools.loadbalancer.RibbonClientRefresh;
import com.netflix.client.config.IClientConfig;
import com.netflix.loadbalancer.ILoadBalancer;
import com.netflix.loadbalancer.IRule;
import org.apache.commons.lang3.BooleanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.context.refresh.ContextRefresher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
@EnableConfigurationProperties({SpringToolsProperties.class})

public class TagRibbonClientConfiguration {


    DelegatingLoadBalancer delegating = new DelegatingLoadBalancer();

    @Bean
    public String delegatingLoadBalancer(IClientConfig clientConfig,
                                         ILoadBalancer lb,
                                         IRule rule,

                                         SpringToolsProperties properties) {

        delegating.setClientConfig(clientConfig);
        delegating.setProperties(properties);
        delegating.setTarget(lb);

        delegating.tagStateInProgress();

        rule.setLoadBalancer(delegating);
        return "delegatingLoadBalancer";
    }

    @Bean
    public String listener(IClientConfig clientConfig, @Autowired(required = false) DelegatePropChangeListener delegatePropChangeListener) {

        PropertiesListener listener = new PropertiesListener(clientConfig.getClientName()+".ribbon", true, (PropertyChangeEvent event) -> {

            delegating.tagStateInProgress();


        });
        delegatePropChangeListener.addListener(listener);


        return "listener";
    }
    @Autowired
    public void ribbonClientRefresh(IClientConfig clientConfig, @Autowired(required = false) DelegatePropChangeListener delegatePropChangeListener, ContextRefresher contextRefresher) {
        String name = clientConfig.getClientName() + ".context.refresh";
        delegatePropChangeListener.addListener(new PropertiesListener(name,true, new PropertiesListener.ChangeListener() {
            @Override
            public void propertyChanged(PropertyChangeEvent event) {
                boolean refresh = BooleanUtils.toBoolean(event.getValue());
                if (refresh) {
                    contextRefresher.refresh();
                }
            }
        }));

    }

}
