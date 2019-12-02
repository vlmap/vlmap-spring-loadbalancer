package com.github.vlmap.spring.tools.loadbalancer.platform.reactor;

import org.springframework.web.server.ServerWebExchange;

public class ReactiveContextHolder {
    private final static ThreadLocal<ServerWebExchange> threadLocal = new ThreadLocal<>();
    public static final String SERVER_WEB_EXCHANGE = "exchange";

    public static ServerWebExchange get( ) {
       return threadLocal.get();

    }

    public static void set(ServerWebExchange  exchange) {
        threadLocal.set(exchange);
    }



    public static void dispose() {
        threadLocal.remove();
    }



}
