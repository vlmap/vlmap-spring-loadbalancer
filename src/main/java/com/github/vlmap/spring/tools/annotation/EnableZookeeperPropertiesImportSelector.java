package com.github.vlmap.spring.tools.annotation;

import com.github.vlmap.spring.tools.SpringToolsProperties;
import com.github.vlmap.spring.tools.zookeeper.ZookeeperPropertiesAutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.boot.context.properties.source.ConfigurationPropertyName;
import org.springframework.cloud.commons.util.SpringFactoryImportSelector;
import org.springframework.cloud.zookeeper.config.ZookeeperConfigAutoConfiguration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.Environment;
import org.springframework.core.type.AnnotationMetadata;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


@Order(Ordered.LOWEST_PRECEDENCE - 100)
@AutoConfigureBefore(ZookeeperConfigAutoConfiguration.class)
public class EnableZookeeperPropertiesImportSelector extends SpringFactoryImportSelector<EnableZookeeperProperties> {
    public static final String WATCHER_ENABLED = "spring.cloud.zookeeper.config.watcher.enabled";

    @Override
    public String[] selectImports(AnnotationMetadata metadata) {
        String[] imports = super.selectImports(metadata);
        List<String> importsList = new ArrayList<>(Arrays.asList(imports));
        importsList.add(ZookeeperPropertiesAutoConfiguration.class.getName());


        imports = importsList.toArray(new String[0]);


        return imports;
    }

    @Override
    protected boolean isEnabled() {
        Environment env = getEnvironment();
        boolean watchEnable = env.getProperty(WATCHER_ENABLED, Boolean.class, true);
        if (watchEnable) return false;
        SpringToolsProperties properties = new SpringToolsProperties();
        Binder.get(env).bind(ConfigurationPropertyName.of("spring.tools"), Bindable.ofInstance(properties));

        return properties.getZookeeper().isEnabled();
    }

    @Override
    protected boolean hasDefaultFactory() {
        return true;
    }
}
