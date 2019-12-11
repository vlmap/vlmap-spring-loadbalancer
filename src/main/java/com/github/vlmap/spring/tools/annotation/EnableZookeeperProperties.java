package com.github.vlmap.spring.tools.annotation;

import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * 启动zookeeper 对 defaultToolsProperties PropertySource  配置
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(EnableZookeeperPropertiesImportSelector.class)
public @interface EnableZookeeperProperties {
}
