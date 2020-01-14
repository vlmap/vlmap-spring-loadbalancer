package com.github.vlmap.spring.loadbalancer.core.platform.reactive;

import com.github.vlmap.spring.loadbalancer.GrayLoadBalancerProperties;
import com.github.vlmap.spring.loadbalancer.core.cli.GaryAttachParamater;
import com.github.vlmap.spring.loadbalancer.core.cli.GrayAttachCommandLineParser;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.bind.BindResult;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.boot.web.reactive.filter.OrderedWebFilter;
import org.springframework.cloud.context.environment.EnvironmentChangeEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.Ordered;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpCookie;
import org.springframework.http.HttpHeaders;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.*;
import java.util.regex.Pattern;

public class GrayAttachReactiveWebFilter implements OrderedWebFilter {
    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private GrayLoadBalancerProperties properties;


    private Environment environment;
    List<GaryAttachParamater> attachs = Collections.emptyList();

    public GrayAttachReactiveWebFilter(GrayLoadBalancerProperties properties, Environment environment) {

        this.properties = properties;
        this.environment = environment;
    }

    String key = "vlmap.spring.loadbalancer.attach.el[0]";


    private static final Pattern QUERY_PATTERN = Pattern.compile("(vlmap.gray.attch[^&=]+)(=?)([^&]+)?");

    GrayAttachCommandLineParser parser = new GrayAttachCommandLineParser();

    @EventListener(EnvironmentChangeEvent.class)
    public void listener(EnvironmentChangeEvent event) {
        List<GaryAttachParamater> list = new ArrayList<>();
        Set<String> keys = event.getKeys();


        BindResult<List<String>> result = Binder.get(environment).bind("vlmap.spring.loadbalancer.attach.el", Bindable.listOf(String.class));
        List<String> expressions = result.orElse(null);

        if (CollectionUtils.isNotEmpty(expressions)) {
            for (String expression : expressions) {

                GaryAttachParamater paramater = parser.parser(expression);
                if (paramater != null) {
                    list.add(paramater);
                }


            }
        }


        this.attachs = list;

    }


    protected String attach(GaryAttachParamater paramater, ServerWebExchange exchange) {
        boolean strict = paramater.isStrict();


    }

    protected String allAttach(GaryAttachParamater paramater, ServerWebExchange exchange) {
        Map<String, String> map = paramater.getCookies();

        if (MapUtils.isNotEmpty(map)) {
            MultiValueMap<String, HttpCookie> cookies = exchange.getRequest().getCookies();

            for (Map.Entry<String, String> entry : map.entrySet()) {
                List<HttpCookie> list = cookies.get(entry.getKey());
                String value = list.stream().filter(cookie -> StringUtils.equals(cookie.getValue(), entry.getValue())).map(cookie -> cookie.getValue()).findFirst().orElse(null);
                if (value == null) {
                    return null;
                }
            }


        }
        map = paramater.getHeaders();

        if (MapUtils.isNotEmpty(map)) {
            HttpHeaders headers = exchange.getRequest().getHeaders();

            for (Map.Entry<String, String> entry : map.entrySet()) {

                List<String> list = headers.get(entry.getKey());
                if(CollectionUtils.isNotEmpty(list)){
                    if(!list.contains(entry.getValue())) {
                        return null;
                    }
                }else {
                    return null;
                }

            }


        }
        List<String> list= paramater.getMethods();
        if(CollectionUtils.isNotEmpty(list)){
            if(!list.contains(exchange.getRequest().getMethod().name())){
                return null;
            }
        }

        Map<String, String> params= paramater.getParams();
        if(MapUtils.isNotEmpty(params)){
            MultiValueMap<String,String> _params=new LinkedMultiValueMap();
            _params.addAll(exchange.getRequest().getQueryParams());

        }


        return   null;
    }

    protected boolean container()


    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {


        return chain.filter(exchange);
    }


    @Override
    public int getOrder() {
        return Ordered.HIGHEST_PRECEDENCE;
    }
}
