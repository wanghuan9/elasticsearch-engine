package com.elasticsearch.engine.base.mapping.handler;

/**
* @author wanghuan
* @description EsConditionHandle
* @mail 958721894@qq.com       
* @date 2022/6/9 14:11 
*/
public interface EsConditionHandle<T> {

    /**
     * if The val is useful
     *
     * @param val return
     */
    boolean test(T val);

}
