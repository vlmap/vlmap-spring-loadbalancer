package com.github.vlmap.spring.loadbalancer.annotation;

import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * 启用灰度路由
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(EnableGrayLoadBalancerImportSelector.class)
public @interface EnableGrayLoadBalancer {
}
