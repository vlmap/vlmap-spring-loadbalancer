package com.github.vlmap.boot;

import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.context.event.ApplicationContextInitializedEvent;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.boot.context.properties.source.ConfigurationPropertyName;
import org.springframework.context.ApplicationListener;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.MutablePropertySources;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class InitializedEventListener implements ApplicationListener<ApplicationContextInitializedEvent> {
    private static final String LOGGING_PATH = "logging.path";
    private static final String APPLICATION_NAME = "spring.application.name";
    private static final String SERVER_PORT = "server.port";

    public static final String EUREKA_SERVICE_URL = "eureka.client.serviceUrl.defaultZone";
    public static final String EUREKA_INSTANCE_IP_ADDRESS = "eureka.instance.ipAddress";
    public static final String EUREKA_INSTANCE_PREFER_IP_ADDRESS = "eureka.instance.preferIpAddress";
    //@formatter:off

    /**
     * #####微服务环境所有机器IP########
     * host:
     *  instance:
     *   - 10.206.2.253
     *   - 10.206.2.238
     *   - 172.18.70.27
     * #####注册发现机器########
     *  discover:
     *   - http://${host.instance[0]}:8761
     *   - https://${host.instance[1]}:8761
     * <p>
     * <p>
     * <p>
     * #####使用SpringApplicationPatch.patch(class)后以下配置不再需要########
     * #eureka:
     * #  client:
     * #    serviceUrl:
     * #      defaultZone: http://${discover[0]}/eureka/,http://${discover[1]}:8761/eureka/
     * #  instance:
     * #    preferIpAddress: true
     *
     * @param
     * @return
     */
    //@formatter:on
    @Override
    public void onApplicationEvent(ApplicationContextInitializedEvent event) {
        ConfigurableEnvironment environment = event.getApplicationContext().getEnvironment();


        MutablePropertySources sources = environment.getPropertySources();


        Map<String, Object> map = new HashMap<String, Object>();
        sources.addFirst(new MapPropertySource("patch", map));

//        CloudHost properties = new CloudHost();
//        Binder.get(environment).bind(ConfigurationPropertyName.of("host"), Bindable.ofInstance(properties));
//
//        String eurekaServiceUrl = environment.getProperty(EUREKA_SERVICE_URL);
//
//        if (StringUtils.isBlank(eurekaServiceUrl)) {
//            eurekaServiceUrl = properties.getEurekaServiceUrl();
//            if (StringUtils.isNotBlank(eurekaServiceUrl)) {
//                map.put(EUREKA_SERVICE_URL, eurekaServiceUrl);
//
//            }
//        }
//
//
//        String eurekaIpAddress = environment.getProperty(EUREKA_INSTANCE_IP_ADDRESS);
//        if (StringUtils.isBlank(eurekaIpAddress)) {
//            eurekaIpAddress = properties.getEurekaInstanceIpAddress();
//            if (StringUtils.isNotBlank(eurekaIpAddress)) {
//                map.put(EUREKA_INSTANCE_IP_ADDRESS, eurekaIpAddress);
//            }
//            map.put(EUREKA_INSTANCE_PREFER_IP_ADDRESS, Boolean.TRUE.toString());
//
//        }
        String loggingPath = environment.getProperty(LOGGING_PATH);
        if (StringUtils.isBlank(loggingPath)) {
            String applicationName = StringUtils.defaultIfBlank(environment.getProperty(APPLICATION_NAME), "");
            String port = StringUtils.defaultIfBlank(environment.getProperty(SERVER_PORT), "8080");
            loggingPath = getFile(new File("").getAbsoluteFile(), "logs", applicationName, port).getAbsolutePath();

            map.put(LOGGING_PATH, loggingPath);
        }

    }


    private static File getFile(final File directory, final String... names) {
        if (directory == null) {
            throw new NullPointerException("directory must not be null");
        }
        if (names == null) {
            throw new NullPointerException("names must not be null");
        }
        File file = directory;
        for (final String name : names) {
            if (StringUtils.isNotBlank(name)) {
                file = new File(file, name);
            }

        }
        return file;
    }
}
