package org.springframework.boot.actuate.endpoint;


/**
 * 适配类，打包时class文件会被删除
 *
 *
 */
public abstract class AbstractEndpoint<T> {
    public AbstractEndpoint(String id) {
        this(id, true);
    }


    public AbstractEndpoint(String id, boolean sensitive) {

    }

    public abstract T invoke();
}
