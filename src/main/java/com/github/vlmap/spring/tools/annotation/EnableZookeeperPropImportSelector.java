package com.github.vlmap.spring.tools.annotation;

import com.github.vlmap.spring.tools.DynamicToolProperties;
import com.github.vlmap.spring.tools.SpringToolsProperties;
import com.github.vlmap.spring.tools.loadbalancer.config.RibbonClientSpecificationAutoConfiguration;
import com.github.vlmap.spring.tools.loadbalancer.platform.feign.TagFeignAutoConfiguration;
import com.github.vlmap.spring.tools.loadbalancer.platform.reactor.TagReactorAutoConfiguration;
import com.github.vlmap.spring.tools.loadbalancer.platform.resttemplate.TagRestTemplateAutoConfiguration;
import com.github.vlmap.spring.tools.loadbalancer.platform.webclient.TagWebClientAutoConfiguration;
import com.github.vlmap.spring.tools.loadbalancer.platform.zuul.TagZuulAutoConfiguration;
import com.github.vlmap.spring.tools.zookeeper.ZookeeperPropAutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.boot.context.properties.source.ConfigurationPropertyName;
import org.springframework.cloud.commons.util.SpringFactoryImportSelector;
import org.springframework.cloud.zookeeper.config.ZookeeperConfigAutoConfiguration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.Environment;
import org.springframework.core.env.PropertySource;
import org.springframework.core.type.AnnotationMetadata;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;


@Order(Ordered.LOWEST_PRECEDENCE - 100)
@AutoConfigureBefore(ZookeeperConfigAutoConfiguration.class)
public class EnableZookeeperPropImportSelector extends SpringFactoryImportSelector<EnableZookeeperProp> {

    SpringToolsProperties properties=  new SpringToolsProperties();

    @Override
    public String[] selectImports(AnnotationMetadata metadata) {
        String[] imports = super.selectImports(metadata);
        List<String> importsList = new ArrayList<>(Arrays.asList(imports));
        importsList.add(ZookeeperPropAutoConfiguration.class.getName());

        DynamicToolProperties dynamicToolProperties= new DynamicToolProperties(getEnvironment(),properties);
        dynamicToolProperties.doAfterPropertiesSet();
        dynamicToolProperties.getDefaultToolsProps().getSource().put("spring.tools.zookeeper.enabled",String.valueOf(properties.getZookeeper().isEnabled()));

        imports = importsList.toArray(new String[0]);



        return imports;
    }

    @Override
    protected boolean isEnabled() {
        Environment env = getEnvironment();
        Binder.get(env).bind(ConfigurationPropertyName.of("spring.tools"), Bindable.ofInstance(properties));

        return properties.getZookeeper().isEnabled();
    }

    @Override
    protected boolean hasDefaultFactory() {
        return true;
    }
}
