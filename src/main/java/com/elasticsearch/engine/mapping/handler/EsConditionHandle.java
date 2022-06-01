package com.elasticsearch.engine.mapping.handler;

/**
 * project  elasticsearch-helper
 * packages   org.pippi.elasticsearch.helper.core
 * date     2021/12/3
 * author   JohenTeng
 * email    1078481395@qq.com
 **/
public interface EsConditionHandle<T> {

    /**
     * if The val is useful
     *
     * @param val return
     */
    boolean test(T val);

}
