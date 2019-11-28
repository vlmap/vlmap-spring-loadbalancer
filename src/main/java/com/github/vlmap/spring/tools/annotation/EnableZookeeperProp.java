package com.github.vlmap.spring.tools.annotation;

import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(EnableZookeeperPropImportSelector.class)
public @interface EnableZookeeperProp {
}
