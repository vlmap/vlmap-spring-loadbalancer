package com.github.vlmap.cloud.loadbalancer.tag;

public interface TagProcess {
    String LOADBALANCER_TAG_HEADER = "Loadbalancer-Tag";
    String LOADBALANCER_TAG = "loadbalancer.tag";

    String getTag();

    void setTag(String tag);

}
