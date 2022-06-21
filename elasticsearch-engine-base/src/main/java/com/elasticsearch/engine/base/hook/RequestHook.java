package com.elasticsearch.engine.base.hook;


import com.elasticsearch.engine.base.holder.AbstractEsRequestHolder;

/**
* @author wanghuan
* @description RequestHook
* @mail 958721894@qq.com       
* @date 2022/6/9 14:10 
*/
@FunctionalInterface
public interface RequestHook<PARAM> {

    /**
     * user define the operation of request
     * you can extend-define Es-request or
     * define aggregation
     *
     * @param holder
     * @param param  return
     */
    AbstractEsRequestHolder handleRequest(AbstractEsRequestHolder holder, PARAM param);

}
