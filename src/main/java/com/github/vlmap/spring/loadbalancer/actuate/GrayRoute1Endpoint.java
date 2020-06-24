//package com.github.vlmap.spring.loadbalancer.actuate;
//
//import com.github.vlmap.spring.loadbalancer.core.platform.AttacherFilter;
//import com.github.vlmap.spring.loadbalancer.core.platform.ResponderFilter;
//import com.github.vlmap.spring.loadbalancer.util.EnvironmentUtils;
// import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
//import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;
//import org.springframework.boot.context.properties.ConfigurationProperties;
//import org.springframework.boot.logging.LoggerConfiguration;
//import org.springframework.core.env.ConfigurableEnvironment;
//import org.springframework.core.env.Environment;
//
//import java.util.*;
//
//@ConfigurationProperties(prefix = "endpoints.loggers")
//public class GrayRoute1Endpoint extends org.springframework.boot.actuate.endpoint.AbstractEndpoint<Map<String, Object>> {
//
//
//
//    @Autowired
//    private AttacherFilter attacherFilter;
//    @Autowired
//    private ResponderFilter responderFilter;
//    @Autowired
//    private Environment environment;
//
//    @Override
//    public Map<String, Object> invoke() {
//        Collection<LoggerConfiguration> configurations = this.loggingSystem
//                .getLoggerConfigurations();
//        if (configurations == null) {
//            return Collections.emptyMap();
//        }
//        Map<String, Object> result = new LinkedHashMap<String, Object>();
//        result.put("levels", getLevels());
//        result.put("loggers", getLoggers(configurations));
//        return result;
//    }
//
//    @ReadOperation
//    public Map<String, Object> get() {
//        Map<String, Object> result = new LinkedHashMap<>();
//        if (environment instanceof ConfigurableEnvironment) {
//            Map<String,String> map = EnvironmentUtils.getSubset((ConfigurableEnvironment) environment, "vlmap.spring.loadbalancer", true);
//
//             result.put("properties", new TreeMap<>(map));
//        }
//
//
//        result.put("attach", attacherFilter.getParamaters());
//        result.put("responder", responderFilter.getParamaters());
//        return result;
//    }
//}
