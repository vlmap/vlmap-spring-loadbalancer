package com.github.vlmap.cloud.zookeeper.config;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

public  class ProxyMap implements Map {

    /**
     * The <code>Map</code> to delegate to.
     */
    protected Map map;
    protected  boolean unmodifiable =false;

    /**
     * Constructor that uses the specified map to delegate to.
     * <p>
     * Note that the map is used for delegation, and is not copied. This is
     * different to the normal use of a <code>Map</code> parameter in
     * collections constructors.
     *
     * @param map the <code>Map</code> to delegate to
     */
    public ProxyMap(Map map ,boolean unmodifiable) {
        this.map = map;
        this.unmodifiable = unmodifiable;
    }

    public void setMap(Map map) {

        this.map = map;
    }



    /**
     * Invokes the underlying {@link Map#clear()} method.
     */
    public void clear() {

        if(unmodifiable){
             throw new RuntimeException(" no support method");
        }
        map.clear();
    }

    /**
     * Invokes the underlying {@link Map#containsKey(Object)} method.
     */
    public boolean containsKey(Object key) {
        return map.containsKey(key);
    }

    /**
     * Invokes the underlying {@link Map#containsValue(Object)} method.
     */
    public boolean containsValue(Object value) {
        return map.containsValue(value);
    }

    /**
     * Invokes the underlying {@link Map#entrySet()} method.
     */
    public Set entrySet() {
        return map.entrySet();
    }

    /**
     * Invokes the underlying {@link Map#equals(Object)} method.
     */
    public boolean equals(Object m) {
        return map.equals(m);
    }

    /**
     * Invokes the underlying {@link Map#get(Object)} method.
     */
    public Object get(Object key) {
        return map.get(key);
    }

    /**
     * Invokes the underlying {@link Map#hashCode()} method.
     */
    public int hashCode() {
        return map.hashCode();
    }

    /**
     * Invokes the underlying {@link Map#isEmpty()} method.
     */
    public boolean isEmpty() {
        return map.isEmpty();
    }

    /**
     * Invokes the underlying {@link Map#keySet()} method.
     */
    public Set keySet() {
        return map.keySet();
    }

    /**
     * Invokes the underlying {@link Map#put(Object, Object)} method.
     */
    public Object put(Object key, Object value) {

        if(unmodifiable){
            throw new RuntimeException(" no support method");
        }
        return map.put(key, value);
    }

    /**
     * Invokes the underlying {@link Map#putAll(Map)} method.
     */
    public void putAll(Map t) {

        if(unmodifiable){
            throw new RuntimeException(" no support method");
        }
        map.putAll(t);
    }

    /**
     * Invokes the underlying {@link Map#remove(Object)} method.
     */
    public Object remove(Object key) {
        if(unmodifiable){
            throw new RuntimeException(" no support method");
        }
        return map.remove(key);
    }

    /**
     * Invokes the underlying {@link Map#size()} method.
     */
    public int size() {
        return map.size();
    }

    /**
     * Invokes the underlying {@link Map#values()} method.
     */
    public Collection values() {
        return map.values();
    }

}