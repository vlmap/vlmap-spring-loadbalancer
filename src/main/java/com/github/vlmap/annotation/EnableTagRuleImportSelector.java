package com.github.vlmap.annotation;

 import com.github.vlmap.cloud.loadbalancer.tag.TagProcess;
 import org.apache.commons.lang3.StringUtils;
 import org.springframework.cloud.commons.util.SpringFactoryImportSelector;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.AnnotationAttributes;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.Environment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.type.AnnotationMetadata;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;


@Order(Ordered.LOWEST_PRECEDENCE - 100)
public class EnableTagRuleImportSelector extends SpringFactoryImportSelector<EnableTagRule> {
    @Override
    public String[] selectImports(AnnotationMetadata metadata) {
        String[] imports = super.selectImports(metadata);

        AnnotationAttributes attributes = AnnotationAttributes.fromMap(
                metadata.getAnnotationAttributes(getAnnotationClass().getName(), true));
        List<String> importsList = new ArrayList<>(Arrays.asList(imports));

        importsList.add("com.github.vlmap.cloud.loadbalancer.config.TagRuleAutoConfiguration");
        importsList.add("com.github.vlmap.cloud.loadbalancer.config.GatewayLoadBalancerClientAutoConfiguration");


        imports = importsList.toArray(new String[0]);
//            Environment env = getEnvironment();
//            if (ConfigurableEnvironment.class.isInstance(env)) {
//                ConfigurableEnvironment configEnv = (ConfigurableEnvironment) env;
//                LinkedHashMap<String, Object> map = new LinkedHashMap<>();
//                map.put("ribbon.NFLoadBalancerRuleClassName", "com.github.vlmap.cloud.loadbalancer.TagRule");
//               String tag= env.getProperty(TagProcess.LOADBALANCER_TAG);
//               if(StringUtils.isBlank(tag)){
//                   map.put("loadbalancer.tag","Loadbalancer-Tag");
//               }
//
//                 MapPropertySource propertySource = new MapPropertySource(
//                        "springCloudTagRule", map);
//                configEnv.getPropertySources().addLast(propertySource);
//            }



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
