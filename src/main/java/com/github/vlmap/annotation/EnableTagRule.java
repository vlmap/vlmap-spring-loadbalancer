package com.github.vlmap.annotation;

import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(EnableTagRuleImportSelector.class)
public @interface EnableTagRule {
}
