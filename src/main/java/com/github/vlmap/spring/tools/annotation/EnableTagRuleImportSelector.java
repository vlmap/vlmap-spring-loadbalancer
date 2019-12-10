package com.github.vlmap.spring.tools.annotation;

import com.github.vlmap.spring.tools.SpringToolsProperties;
import com.github.vlmap.spring.tools.loadbalancer.config.RibbonClientSpecificationAutoConfiguration;
import com.github.vlmap.spring.tools.loadbalancer.client.feign.TagFeignAutoConfiguration;
import com.github.vlmap.spring.tools.loadbalancer.platform.gateway.TagReactiveAutoConfiguration;
import com.github.vlmap.spring.tools.loadbalancer.client.resttemplate.TagRestTemplateAutoConfiguration;
import com.github.vlmap.spring.tools.loadbalancer.platform.springmvc.TagSpringmvcAutoConfiguration;
import com.github.vlmap.spring.tools.loadbalancer.client.webclient.TagWebClientAutoConfiguration;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.boot.context.properties.source.ConfigurationPropertyName;
import org.springframework.cloud.commons.util.SpringFactoryImportSelector;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.Environment;
import org.springframework.core.type.AnnotationMetadata;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


@Order(Ordered.LOWEST_PRECEDENCE - 100)

public class EnableTagRuleImportSelector extends SpringFactoryImportSelector<EnableTagRule> {


    @Override
    public String[] selectImports(AnnotationMetadata metadata) {
        String[] imports = super.selectImports(metadata);
        List<String> importsList = new ArrayList<>(Arrays.asList(imports));
        importsList.add(RibbonClientSpecificationAutoConfiguration.class.getName());

        importsList.add(TagReactiveAutoConfiguration.class.getName());
        importsList.add(TagSpringmvcAutoConfiguration.class.getName());

        importsList.add(TagFeignAutoConfiguration.class.getName());
        importsList.add(TagRestTemplateAutoConfiguration.class.getName());
        importsList.add(TagWebClientAutoConfiguration.class.getName());


        imports = importsList.toArray(new String[0]);


        return imports;
    }

    @Override
    protected boolean isEnabled() {
        Environment env = getEnvironment();
        SpringToolsProperties properties = new SpringToolsProperties();

        Binder.get(env).bind(ConfigurationPropertyName.of("spring.tools"), Bindable.ofInstance(properties));

        return properties.getTagLoadbalancer().isEnabled();
    }

    @Override
    protected boolean hasDefaultFactory() {
        return true;
    }
}
