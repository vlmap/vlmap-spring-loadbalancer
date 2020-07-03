package com.github.vlmap.spring.loadbalancer.core.platform;

import com.github.vlmap.spring.loadbalancer.GrayLoadBalancerProperties;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;

import java.util.Collections;
import java.util.List;

public class ResponderFilter extends CommandsListener<ResponderParamater> implements Ordered {
     protected Logger logger = LoggerFactory.getLogger(this.getClass());

    public ResponderFilter(GrayLoadBalancerProperties properties) {

        super(properties);
    }

    @Override
    public Class getParamaterType() {
        return ResponderParamater.class;
    }


    @Override
    protected boolean validate(ResponderParamater paramater) {
        return paramater.isState() && StringUtils.isNotEmpty(paramater.getValue());
    }

    @Override
    protected List<String> getCommands(GrayLoadBalancerProperties properties) {
        return properties.getResponder() == null ? null : properties.getResponder().getCommands();
    }

    public List<ResponderParamater> getParamaters() {
        return getCommandObject();
    }



    protected ResponderParamater getParamater(  String tag) {
        List<ResponderParamater>   list=    getCommandObject();
        if(CollectionUtils.isEmpty(list))return null;
        for (ResponderParamater paramater : list) {
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
