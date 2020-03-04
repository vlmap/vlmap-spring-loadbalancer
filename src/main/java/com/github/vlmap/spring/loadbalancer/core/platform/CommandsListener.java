package com.github.vlmap.spring.loadbalancer.core.platform;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.vlmap.spring.loadbalancer.GrayLoadBalancerProperties;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.cloud.context.environment.EnvironmentChangeEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public abstract class CommandsListener<T extends CommandParamater> {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    protected GrayLoadBalancerProperties properties;
    @Autowired
    private ObjectMapper mapper;


    public CommandsListener(GrayLoadBalancerProperties properties) {

        this.properties = properties;

    }

    public abstract Class<T> getParamaterType();

    protected abstract String getPrefix();

    protected abstract boolean validate(T paramater);

    protected abstract List<String> getCommands(GrayLoadBalancerProperties properties);

    protected abstract void setParamaters(List<T> paramaters);

    @Order
    @EventListener(EnvironmentChangeEvent.class)

    public void listener(EnvironmentChangeEvent event) {
        Set<String> keys = event.getKeys();
        boolean state = false;
        String prefix = getPrefix();
        if (CollectionUtils.isNotEmpty(keys)) {
            for (String key : keys) {
                if (StringUtils.startsWith(key, prefix)) {
                    state = true;
                    break;
                }
            }
        }

        if (state) {
            initParamater();

        }


    }

    @EventListener(ApplicationReadyEvent.class)
    public void initParamater() {
        List<String> input = getCommands(properties);
        Class<T> clazz = getParamaterType();
        if (CollectionUtils.isNotEmpty(input)) {
            List<T> list = new ArrayList<>(input.size());

            for (String expression : input) {

                try {
                    T paramater = mapper.readValue(expression, clazz);
                    if (logger.isInfoEnabled()) {
                        logger.info("command:" + expression + " , " + clazz.getSimpleName() + ":" + paramater.toString());
                    }


                    if (validate(paramater)) {

                        list.add(paramater);


                    }
                } catch (Exception e) {
                    logger.error("", e);
                }


            }
            setParamaters(Collections.unmodifiableList(list));
        } else {
            setParamaters(Collections.emptyList());

        }

    }


}
