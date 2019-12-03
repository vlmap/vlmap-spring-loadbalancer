package com.github.vlmap.spring.tools;

import com.github.vlmap.spring.tools.actuator.PropertiesEndPoint;
import com.github.vlmap.spring.tools.event.listener.DelegatePropChangeListener;
import com.github.vlmap.spring.tools.event.listener.PropertiesListener;
import com.github.vlmap.spring.tools.event.listener.RefreshListener;
import org.springframework.boot.actuate.autoconfigure.endpoint.condition.ConditionalOnEnabledEndpoint;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.boot.context.properties.source.ConfigurationPropertyName;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Configuration
@EnableConfigurationProperties({SpringToolsProperties.class})

public class SpringToolsAutoConfiguration  {

    @Bean
    @ConditionalOnMissingBean

    public DynamicToolProperties dynamicToolProperties(Environment env, SpringToolsProperties properties) {
        return new DynamicToolProperties(env, properties);
    }
    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnEnabledEndpoint
    public PropertiesEndPoint propsEndPoint(DynamicToolProperties properties) {
        return new PropertiesEndPoint(properties);
    }
    @Bean

    public RefreshListener refreshListener() {
        return new RefreshListener();
    }
    @Bean
    public DelegatePropChangeListener delegatePropChangeListener(){
        return new DelegatePropChangeListener();
    }
    @Bean
    public PropertiesListener serverTagListener(Environment env,SpringToolsProperties properties){
        ConfigurationPropertyName propertyName=ConfigurationPropertyName.of("spring.tools.tag-loadbalancer.headers");
        PropertiesListener listener=new PropertiesListener(propertyName,false,(event -> {
           List<String> headers= Binder.get(env).bind(propertyName, Bindable.listOf(String.class)).orElse(null);
           if(headers!=null){
               List _headers=headers.stream().filter(it->it!=null).collect(Collectors.toList());
               properties.getTagLoadbalancer().setHeaders(_headers);

           }
        }));
        return listener;
    }
}
