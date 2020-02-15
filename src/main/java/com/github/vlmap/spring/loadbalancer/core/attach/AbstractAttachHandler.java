package com.github.vlmap.spring.loadbalancer.core.attach;

import com.github.vlmap.spring.loadbalancer.GrayLoadBalancerProperties;
import com.github.vlmap.spring.loadbalancer.core.attach.cli.GaryAttachParamater;
import com.github.vlmap.spring.loadbalancer.core.attach.cli.GrayAttachCommandLineParser;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.context.environment.EnvironmentChangeEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.util.AntPathMatcher;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.*;

public abstract class AbstractAttachHandler {
    private static Logger logger = LoggerFactory.getLogger(AbstractAttachHandler.class);

    protected static final Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;
    protected Environment environment;
    protected List<GaryAttachParamater> attachParamaters = Collections.emptyList();
    protected GrayAttachCommandLineParser parser = new GrayAttachCommandLineParser();
    protected AntPathMatcher pathMatcher = new AntPathMatcher();

    private static final String ATTACH_COMMANDS_PREFIX = "vlmap.spring.loadbalancer.attach.commands";
    protected GrayLoadBalancerProperties properties;
    protected MatcherProcess matcher = new MatcherProcess();

    public AbstractAttachHandler(GrayLoadBalancerProperties properties, Environment environment) {
        this.environment = environment;
        this.properties = properties;
    }

    public List<GaryAttachParamater> getAttachParamaters() {
        return attachParamaters;
    }

    @Order
    @EventListener(EnvironmentChangeEvent.class)

    public void listener(EnvironmentChangeEvent event) {
        Set<String> keys = event.getKeys();
        boolean state = false;
        if (CollectionUtils.isNotEmpty(keys)) {
            for (String key : keys) {
                if (StringUtils.startsWith(key, ATTACH_COMMANDS_PREFIX)) {
                    state = true;
                    break;
                }
            }
        }

        if (state) {
            List<String> commands = properties.getAttach().getCommands();

            Set<GaryAttachParamater> list = new LinkedHashSet<>();

            if (CollectionUtils.isNotEmpty(commands)) {
                for (int i = 0, length = commands.size(); i < length; i++) {
                    String expression = commands.get(i);

                    GaryAttachParamater paramater = parser.parser(expression);
                    if (logger.isInfoEnabled()) {
                        logger.info("command:" + expression + " , GaryAttachParamater:" + paramater.toString());
                    }


                    if (paramater != null && StringUtils.isNotBlank(paramater.getValue())) {
                        list.add(paramater);
                    }


                }
            }

            this.attachParamaters = Collections.unmodifiableList(new ArrayList<>(list));
            if (logger.isInfoEnabled()) {
                logger.info("List  GaryAttachParamater :" + attachParamaters);
            }
        }


    }

    public void match(SimpleRequestData data, List<GaryAttachParamater> paramaters, List<String> result) {
        GaryAttachParamater paramater = this.matcher.match(data, paramaters);
        if (paramater != null) {
            result.add(paramater.getValue());
        }

    }


    public boolean isJsonRequest(MediaType contentType, HttpMethod method) {


        if (!(HttpMethod.GET.equals(method) || HttpMethod.HEAD.equals(method))) {
            if (contentType.isCompatibleWith(MediaType.APPLICATION_JSON)) {

                return true;


            }
        }

        return false;
    }


}
