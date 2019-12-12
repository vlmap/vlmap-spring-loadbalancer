package com.github.vlmap.spring.tools.context.event.listener;

import com.github.vlmap.spring.tools.context.event.PropertyChangeEvent;


public class PropertiesListener {
    public interface ChangeListener {
        void propertyChanged(PropertyChangeEvent event);
    }

    private ChangeListener call;
    private String name;
    //监听以 name 开头的节点
    private boolean prefix = false;
    private String id;

    public PropertiesListener(String name, ChangeListener call) {

        this(name, false, call);
    }

    public ChangeListener getCall() {
        return call;
    }

    public String getName() {
        return name;
    }

    public boolean isPrefix() {
        return prefix;
    }

    /**
     * @param name
     * @param prefix
     * @param call
     */

    public PropertiesListener(String name, boolean prefix, ChangeListener call) {
        this.name = name;
        this.prefix = prefix;
        this.call = call;

        this.id = name;

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }


}
