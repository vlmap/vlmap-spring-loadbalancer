package com.github.vlmap.cloud.loadbalancer.tag;

public interface TagProcess {
 String loadbalancerTag = "Loadbalancer-Tag";
 String LOADBALANCER_TAG = "loadbalancer.tag";

    String getTag();

    void setTag(String tag);

}
