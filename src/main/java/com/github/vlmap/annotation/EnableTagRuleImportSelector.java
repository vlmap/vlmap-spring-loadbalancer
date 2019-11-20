package com.github.vlmap.annotation;

import com.github.vlmap.cloud.loadbalancer.tag.TagProcess;
import org.apache.commons.lang3.StringUtils;
import org.springframework.cloud.commons.util.SpringFactoryImportSelector;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.*;
import org.springframework.core.type.AnnotationMetadata;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;


@Order(Ordered.LOWEST_PRECEDENCE - 100)
public class EnableTagRuleImportSelector extends SpringFactoryImportSelector<EnableTagRule> {
    @Override
    public String[] selectImports(AnnotationMetadata metadata) {
        String[] imports = super.selectImports(metadata);


        List<String> importsList = new ArrayList<>(Arrays.asList(imports));

        importsList.add("com.github.vlmap.cloud.loadbalancer.config.TagRuleAutoConfiguration");
        importsList.add("com.github.vlmap.cloud.loadbalancer.config.GatewayLoadBalancerClientAutoConfiguration");


        imports = importsList.toArray(new String[0]);



        return imports;
    }

    @Override
    protected boolean isEnabled() {
        return true;
//        return getEnvironment().getProperty("spring.cloud.circuit.breaker.enabled",
//                Boolean.class, Boolean.TRUE);
    }

    @Override
    protected boolean hasDefaultFactory() {
        return true;
    }
}
