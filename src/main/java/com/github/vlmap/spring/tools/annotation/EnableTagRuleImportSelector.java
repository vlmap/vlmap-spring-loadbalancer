package com.github.vlmap.spring.tools.annotation;

import com.github.vlmap.spring.tools.DynamicToolProperties;
import com.github.vlmap.spring.tools.SpringToolsProperties;
import com.github.vlmap.spring.tools.loadbalancer.config.RibbonClientSpecificationAutoConfiguration;
import com.github.vlmap.spring.tools.loadbalancer.platform.feign.TagFeignAutoConfiguration;
import com.github.vlmap.spring.tools.loadbalancer.platform.reactor.TagReactorAutoConfiguration;
import com.github.vlmap.spring.tools.loadbalancer.platform.resttemplate.TagRestTemplateAutoConfiguration;
import com.github.vlmap.spring.tools.loadbalancer.platform.webclient.TagWebClientAutoConfiguration;
import com.github.vlmap.spring.tools.loadbalancer.platform.zuul.TagZuulAutoConfiguration;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.boot.context.properties.source.ConfigurationPropertyName;
import org.springframework.cloud.commons.util.SpringFactoryImportSelector;
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
public class EnableTagRuleImportSelector extends SpringFactoryImportSelector<EnableTagRule> {

    SpringToolsProperties properties=  new SpringToolsProperties();

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

        DynamicToolProperties dynamicToolProperties= new DynamicToolProperties(getEnvironment(),properties);
        dynamicToolProperties.doAfterPropertiesSet();
        PropertySource propertySource = dynamicToolProperties.getDefaultToolsProps();

        if(propertySource!=null&&Map.class.isInstance(propertySource.getSource())){
            Map<String,String>  map=(Map)propertySource.getSource();
            if (map != null) {
                String object = properties.getPropertySourceName();
                if (object != null) {
                    map.put("spring.tools.property-source-name", object);
                }

                map.put("spring.tools.tag-load-balancer.enabled",String.valueOf(properties.getTagLoadBalancer().isEnabled()));
                map.put("spring.tools.tag-load-balancer.feign.enabled",String.valueOf(properties.getTagLoadBalancer().getFeign().isEnabled()));
                map.put("spring.tools.tag-load-balancer.rest-template.enabled",String.valueOf(properties.getTagLoadBalancer().getRestTemplate().isEnabled()));
                map.put("spring.tools.tag-load-balancer.web-client.enabled",String.valueOf(properties.getTagLoadBalancer().getWebClient().isEnabled()));

                object = properties.getTagLoadBalancer().getHeader();
                if (object != null) {
                    map.put("spring.tools.tag-load-balancer.header", object);
                }

                object = properties.getTagLoadBalancer().getHeaderName();
                if (object != null) {
                    map.put("spring.tools.tag-load-balancer.header-name", object);
                }



            }
        }

        return imports;
    }

    @Override
    protected boolean isEnabled() {
        Environment env = getEnvironment();
        Binder.get(env).bind(ConfigurationPropertyName.of("spring.tools"), Bindable.ofInstance(properties));

        return properties.getTagLoadBalancer().isEnabled();
    }

    @Override
    protected boolean hasDefaultFactory() {
        return true;
    }
}
