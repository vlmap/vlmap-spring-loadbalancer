package com.github.vlmap.spring.tools.annotation;

import com.github.vlmap.spring.tools.loadbalancer.config.RibbonClientSpecificationAutoConfiguration;
import org.springframework.cloud.commons.util.SpringFactoryImportSelector;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.*;
import org.springframework.core.type.AnnotationMetadata;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


@Order(Ordered.LOWEST_PRECEDENCE - 100)
public class EnableTagRuleImportSelector extends SpringFactoryImportSelector<EnableTagRule> {
    private static final String PROPERTY_SOURCE_NAME = "spring.tools.property-source-name";
    private static final String TAG_RULE_HEADER_NAME = "spring.tools.tag-rule.header-name";


    @Override
    public String[] selectImports(AnnotationMetadata metadata) {
        String[] imports = super.selectImports(metadata);


        List<String> importsList = new ArrayList<>(Arrays.asList(imports));
        importsList.add(RibbonClientSpecificationAutoConfiguration.class.getName());


        imports = importsList.toArray(new String[0]);
        Environment env = getEnvironment();
        String propertySourceName = env.getProperty(PROPERTY_SOURCE_NAME, "defaultStateProps");
        String loadbalancerTag = env.getProperty(TAG_RULE_HEADER_NAME, "Loadbalancer-Tag");


        if (env instanceof ConfigurableEnvironment) {
            ConfigurableEnvironment configEnv = (ConfigurableEnvironment) env;
            PropertySource propertySource = configEnv.getPropertySources().get(propertySourceName);

            Map map = null;
            if (propertySource == null) {
                map = new ConcurrentHashMap<>();

                propertySource = new MapPropertySource(propertySourceName, map);
                configEnv.getPropertySources().addLast(propertySource);

            } else if (propertySource instanceof MapPropertySource) {
                MapPropertySource object = (MapPropertySource) propertySource;
                if (object.getSource() instanceof Map) {
                    map = object.getSource();
                }
            } else if (!(propertySource instanceof MapPropertySource) && propertySource instanceof EnumerablePropertySource) {
                map = new ConcurrentHashMap<>();

                EnumerablePropertySource enumerablePropertySource = (EnumerablePropertySource) propertySource;
                String[] names = enumerablePropertySource.getPropertyNames();
                if (names != null) {
                    for (String name : names) {
                        map.put(name, propertySource.getProperty(name));
                    }
                }
                configEnv.getPropertySources().replace(propertySourceName, propertySource);


            }
            if (map != null) {
                map.put(PROPERTY_SOURCE_NAME, propertySourceName);
                map.put("spring.tools.tag-rule.enabled", "true");
                map.put(TAG_RULE_HEADER_NAME, loadbalancerTag);


            }


        }
        return imports;
    }

    @Override
    protected boolean isEnabled() {

        return getEnvironment().getProperty("spring.tools.tag-rule.enabled",
                Boolean.class, Boolean.TRUE);
    }

    @Override
    protected boolean hasDefaultFactory() {
        return true;
    }
}
