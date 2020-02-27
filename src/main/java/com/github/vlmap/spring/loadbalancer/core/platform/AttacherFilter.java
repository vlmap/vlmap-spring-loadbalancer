package com.github.vlmap.spring.loadbalancer.core.platform;

import com.github.vlmap.spring.loadbalancer.GrayLoadBalancerProperties;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.Ordered;

import java.util.Collections;
import java.util.List;

public class AttacherFilter extends CommandsListener<RequestMatchParamater> implements Ordered {

    protected List<RequestMatchParamater> paramaters = Collections.emptyList();

    private static final String ATTACH_COMMANDS_PREFIX = "vlmap.spring.loadbalancer.attach.commands";
    protected MatcherProcess matcher = new MatcherProcess();


    public AttacherFilter(GrayLoadBalancerProperties properties) {
        super(properties);
    }

    @Override
    public Class<RequestMatchParamater> getParamaterType() {
        return RequestMatchParamater.class;
    }

    @Override
    protected String getPrefix() {
        return ATTACH_COMMANDS_PREFIX;
    }

    @Override
    protected boolean validate(RequestMatchParamater paramater) {
        return paramater.isState()&&StringUtils.isNotBlank(paramater.getValue());
    }

    @Override
    protected List<String> getCommands(GrayLoadBalancerProperties properties) {
        return properties.getAttacher().getCommands();
    }

    @Override
    protected void setParamaters(List<RequestMatchParamater> paramaters) {
        this.paramaters = paramaters;

    }

    public List<RequestMatchParamater> getParamaters() {
        return paramaters;
    }

    @Override
    public int getOrder() {
        return FilterOrder.ORDER_ATTACH_FILTER;
    }


}
