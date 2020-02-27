package com.github.vlmap.spring.loadbalancer.core.platform;


public abstract class CommandParamater {


    //启用/禁用 ,如果  state 不为空 则仅更新状态
    private boolean state = true;

    public boolean isState() {
        return state;
    }

    public void setState(boolean state) {
        this.state = state;
    }
}