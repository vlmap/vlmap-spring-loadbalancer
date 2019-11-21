package com.github.vlmap.spring.tools.cloud.zookeeper.config;

import org.apache.commons.lang3.StringUtils;
import org.apache.curator.framework.CuratorFramework;
import org.apache.zookeeper.CreateMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ConfigAttachWriter {
    private final Logger log = LoggerFactory.getLogger(ConfigAttachWriter.class);
    private String defaultContext;
    private List<String> context;

    private CuratorFramework curator;


    public void setCurator(CuratorFramework curator) {
        this.curator = curator;
    }

    public void setDefaultContext(String defaultContext) {
        this.defaultContext = defaultContext;
    }


    public void setContext(List<String> context) {
        this.context = context;
    }

    public void write(CreateMode mode, String[] paths, String value) {
        this.write(mode, this.defaultContext, paths, value);
    }

    public void write(CreateMode mode, String context, String[] paths, String value) {

        String path = toPath(context, paths);
        try {

            curator.create().orSetData().creatingParentContainersIfNeeded().withMode(mode != null ? CreateMode.PERSISTENT : mode).forPath(path, value.getBytes(StandardCharsets.UTF_8));

        } catch (Exception e) {
            log.error("write path:" + path + ",value:" + value + " faild!", e);
        }

    }


    public void delete(String context, String... paths) {

        String path = toPath(context, paths);
        try {
            curator.delete().forPath(path);
        } catch (Exception e) {
            log.error("delete path:" + path + " faild!", e);
        }
    }

    private String toPath(String context, String... args) {
        StringBuilder builder = new StringBuilder("/");
        List<String> list = new ArrayList<>(args.length + 1);
        list.add(context);
        list.addAll(Arrays.asList(args));
        for (String element : list) {
            char last = builder.charAt(builder.length() - 1);

            if (last != '/') {
                builder.append("/");
            }
            if (StringUtils.startsWith(element, "/")) {
                builder.append(element.substring(1));
            } else {
                builder.append(element);
            }


        }
        return builder.toString();
    }

}
