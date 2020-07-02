package com.github.vlmap.spring.loadbalancer.core.platform;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.vlmap.spring.loadbalancer.GrayLoadBalancerProperties;
import com.github.vlmap.spring.loadbalancer.common.MapCache;
import org.apache.commons.collections.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class CommandsListener<T extends CommandParamater> {

    protected GrayLoadBalancerProperties properties;
    private Logger logger = LoggerFactory.getLogger(this.getClass());
    @Autowired
    private ObjectMapper mapper;




    private MapCache<Object, List<T>> cacheObject = new MapCache(new MapCacheMirror());
    public CommandsListener(GrayLoadBalancerProperties properties) {

        this.properties = properties;

    }

    public abstract Class<T> getParamaterType();


    protected abstract boolean validate(T paramater);

    protected abstract List<String> getCommands(GrayLoadBalancerProperties properties);


    protected List<T> getCommandObject() {
        List<String> key = getCommands(properties);
        if (CollectionUtils.isEmpty(key)) return null;
        return cacheObject.get(key);
    }



    class MapCacheMirror implements MapCache.Mirror<Object, List<T>> {

        @Override

        public List<T> invoker(Object key) {
            List<String> commands = (List) key;
            if (CollectionUtils.isEmpty(commands)) return null;

            Class<T> clazz = getParamaterType();
            List<T> list = new ArrayList<>(commands.size());

            for (String expression : commands) {

                try {
                    T paramater = mapper.readValue(expression, clazz);
                    if (logger.isInfoEnabled()) {
                        logger.info("command:" + expression + " , " + clazz.getSimpleName() + ":" + paramater.toString());
                    }


                    if (validate(paramater)) {

                        list.add(paramater);


                    }
                } catch (Exception e) {
                    logger.error("", e);
                }


            }
            return Collections.unmodifiableList(list);

        }
    }

}
