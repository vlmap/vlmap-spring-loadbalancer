package com.github.vlmap.spring.loadbalancer.core.platform;

import com.github.vlmap.spring.loadbalancer.GrayLoadBalancerProperties;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;

import java.util.Collections;
import java.util.List;

public class ResponderFilter extends CommandsListener<ResponderParamater> implements Ordered {
    protected Logger logger = LoggerFactory.getLogger(this.getClass());


    private static final String RESPONDER_COMMANDS_PREFIX = "vlmap.spring.loadbalancer.responder.commands";


    protected List<ResponderParamater> paramaters = Collections.emptyList();

    public ResponderFilter(GrayLoadBalancerProperties properties) {

        super(properties);
    }

    @Override
    public Class getParamaterType() {
        return ResponderParamater.class;
    }

    @Override
    protected String getPrefix() {
        return RESPONDER_COMMANDS_PREFIX;
    }

    @Override
    protected boolean validate(ResponderParamater paramater) {
        return paramater.isState() && StringUtils.isNotEmpty(paramater.getValue());
    }

    @Override
    protected List<String> getCommands(GrayLoadBalancerProperties properties) {
        return properties.getResponder()==null?null: properties.getResponder().getCommands();
    }

    @Override
    protected void setParamaters(List list) {
        paramaters = list;
    }

    public List<ResponderParamater> getParamaters() {
        return paramaters;
    }

    protected ResponderParamater getParamater(List<ResponderParamater> paramaters, String tag) {
        for (ResponderParamater paramater : paramaters) {
            if (StringUtils.equals(tag, paramater.getValue())) {
                return paramater;
            }
        }
        return null;
    }

    public int getOrder() {
        return FilterOrder.ORDER_RESPONDER_FILTER;
    }

}
