package com.github.vlmap.spring.loadbalancer.util;


import com.netflix.util.HashCode;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.context.properties.bind.Bindable;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.boot.context.properties.source.ConfigurationPropertySource;
import org.springframework.core.env.ConfigurableEnvironment;

import java.io.Serializable;
import java.util.*;

public class GrayUtils {


    public static Map<String, Set<String>> tagOfServer(ConfigurableEnvironment environment, String clientName) {
        clientName = StringUtils.upperCase(clientName);

        ConfigurationPropertySource propertySource = EnvironmentUtils.getSubsetConfigurationPropertySource(environment, clientName);


        Binder binder = new Binder(propertySource);
        GrayTagOfServersProperties ribbon = new GrayTagOfServersProperties();
        binder.bind("ribbon", Bindable.ofInstance(ribbon));

        List<TagOfServers> tagOfServers = ribbon.getGray();
        if (tagOfServers != null) {
            Map<String, Set<String>> map = new HashMap<>(tagOfServers.size());

            for (TagOfServers tagOfServer : tagOfServers) {

                if (tagOfServer != null && CollectionUtils.isNotEmpty(tagOfServer.getTags()) && StringUtils.isNotBlank(tagOfServer.getId())) {
                    Pair<String, Integer> hostPort = getHostPort(tagOfServer.getId());

                    String id =hostPort.first() + ":" + hostPort.second();

                    map.put(id, tagOfServer.getTags());
                }
            }
            return Collections.unmodifiableMap(map);


        }
        return Collections.emptyMap();
    }
    public static Pair<String, Integer> getHostPort(String id) {
        if (id != null) {
            String host = null;
            int port = 80;

            if (id.toLowerCase().startsWith("http://")) {
                id = id.substring(7);
                port = 80;
            } else if (id.toLowerCase().startsWith("https://")) {
                id = id.substring(8);
                port = 443;
            }

            if (id.contains("/")) {
                int slash_idx = id.indexOf("/");
                id = id.substring(0, slash_idx);
            }

            int colon_idx = id.indexOf(':');

            if (colon_idx == -1) {
                host = id; // default
            } else {
                host = id.substring(0, colon_idx);
                try {
                    port = Integer.parseInt(id.substring(colon_idx + 1));
                } catch (NumberFormatException e) {
                    throw e;
                }
            }
            return new Pair<String, Integer>(host, port);
        } else {
            return null;
        }

    }


    /**
     * A simple class that holds a pair of values.
     * This may be useful for methods that care to
     * return two values (instead of just one).
     */
    public static class Pair<E1, E2> implements Serializable {

        // ========================================
        // Static vars: public, protected, then private
        // ========================================
        private static final long serialVersionUID = 2L;

        // ========================================
        // Instance vars: public, protected, then private
        // ========================================

        private E1 mFirst;
        private E2 mSecond;

        // ========================================
        // Constructors
        // ========================================

        /**
         * Construct a new pair
         *
         * @param first  the object to store as the first value
         * @param second the object to store as the second value
         */
        public Pair(E1 first, E2 second) {
            mFirst = first;
            mSecond = second;
        }

        // ========================================
        // Methods, grouped by functionality, *not* scope
        // ========================================

        /**
         * Get the first value from the pair.
         *
         * @return the first value
         */
        public E1 first() {
            return mFirst;
        }

        /**
         * Get the second value from the pair.
         *
         * @return the second value
         */
        public E2 second() {
            return mSecond;
        }

        /**
         * Set the first value of the pair.
         *
         * @param first the new first value
         */
        public void setFirst(E1 first) {
            mFirst = first;
        }

        /**
         * Set the second value of the pair.
         *
         * @param second the new second value
         */
        public void setSecond(E2 second) {
            mSecond = second;
        }

        // ----------------------------------------
        // Generic Object methods

        /**
         * Pair objects are equal iff they have the same content.
         */
        @Override
        public boolean equals(Object obj) {
            if (obj == this) {
                return true;
            } else if (obj == null || obj.getClass() != getClass()) {
                return false;
            }
            Pair other = (Pair) obj;
            return HashCode.equalObjects(mFirst, other.mFirst)
                    && HashCode.equalObjects(mSecond, other.mSecond);
        }

        // The hash code needs to align with the
        // definition of equals.
        @Override
        public int hashCode() {
            HashCode h = new HashCode();
            h.addValue(mFirst);
            h.addValue(mSecond);
            return h.hashCode();
        }

    } // Pair

    /**
     * 灰度路由，服务配置
     */
//@ConfigurationProperties(prefix = "MICRO-CLOUD-SERVER.ribbon")
    public static class GrayTagOfServersProperties {


        List<TagOfServers> gray;

        public List<TagOfServers> getGray() {
            return gray;
        }

        public void setGray(List<TagOfServers> gray) {
            this.gray = gray;
        }
    }

    public static class TagOfServers {
        private String id;
        private Set<String> tags;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public Set<String> getTags() {
            return tags;
        }

        public void setTags(Set<String> tags) {
            this.tags = tags;
        }
    }

}
