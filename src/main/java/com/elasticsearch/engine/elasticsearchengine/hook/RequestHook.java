package com.elasticsearch.engine.elasticsearchengine.hook;


import com.elasticsearch.engine.elasticsearchengine.holder.AbstractEsRequestHolder;

/**
 * author     JohenTeng
 * date      2021/7/21
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
