package com.github.vlmap.spring.tools.actuator;

import com.github.vlmap.spring.tools.DynamicToolProperties;
import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.boot.actuate.endpoint.annotation.ReadOperation;

import java.util.Map;
import java.util.TreeMap;

@Endpoint(
        id = "props"
)
public class ProspEndPoint {
    private DynamicToolProperties properties;

    public ProspEndPoint(DynamicToolProperties properties) {
        this.properties = properties;
    }

    @ReadOperation
    public Response mappings() {

        return new Response(new TreeMap(properties.getDefaultToolsProps().getSource()));
    }
    public static class Response {
        Map contexts;

        public Response(Map contexts) {
            this.contexts = contexts;
        }

        public Map getContexts() {
            return contexts;
        }

        public void setContexts(Map contexts) {
            this.contexts = contexts;
        }
    }
}
