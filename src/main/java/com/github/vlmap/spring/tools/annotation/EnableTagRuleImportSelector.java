package com.github.vlmap.spring.tools.annotation;

import com.github.vlmap.spring.tools.SpringToolsAutoConfiguration;
import com.github.vlmap.spring.tools.SpringToolsProperties;
import com.github.vlmap.spring.tools.loadbalancer.config.RibbonClientSpecificationAutoConfiguration;
import com.github.vlmap.spring.tools.loadbalancer.platform.feign.TagFeignAutoConfiguration;
import com.github.vlmap.spring.tools.loadbalancer.platform.reactor.TagReactorAutoConfiguration;
import com.github.vlmap.spring.tools.loadbalancer.platform.resttemplate.TagRestTemplateAutoConfiguration;
import com.github.vlmap.spring.tools.loadbalancer.platform.webclient.TagWebClientAutoConfiguration;
import com.github.vlmap.spring.tools.loadbalancer.platform.zuul.TagZuulAutoConfiguration;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
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
@AutoConfigureAfter(SpringToolsAutoConfiguration.class)

public class EnableTagRuleImportSelector extends SpringFactoryImportSelector<EnableTagRule> {


    @Override
    public String[] selectImports(AnnotationMetadata metadata) {
        String[] imports = super.selectImports(metadata);
        List<String> importsList = new ArrayList<>(Arrays.asList(imports));
        importsList.add(RibbonClientSpecificationAutoConfiguration.class.getName());

        importsList.add(TagReactorAutoConfiguration.class.getName());
        importsList.add(TagFeignAutoConfiguration.class.getName());
        importsList.add(TagRestTemplateAutoConfiguration.class.getName());
        importsList.add(TagZuulAutoConfiguration.class.getName());
        importsList.add(TagWebClientAutoConfiguration.class.getName());


        imports = importsList.toArray(new String[0]);


        return imports;
    }

    @Override
    protected boolean isEnabled() {
        Environment env = getEnvironment();
        SpringToolsProperties properties=new SpringToolsProperties();
        Binder.get(env).bind(ConfigurationPropertyName.of("spring.tools"), Bindable.ofInstance(properties));

        return properties.getTagLoadbalancer().isEnabled();
     }

    @Override
    protected boolean hasDefaultFactory() {
        return true;
    }
}
