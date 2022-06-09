package com.elasticsearch.engine.hook;

import org.elasticsearch.action.search.SearchResponse;

/**
* @author wanghuan
* @description ResponseHook
* @mail 958721894@qq.com       
* @date 2022/6/9 14:10 
*/
@FunctionalInterface
public interface ResponseHook<RESULT> {

    /**
     * user define the method to handle ElasticSearch-Response
     *
     * @param resp return
     */
    RESULT handleResponse(SearchResponse resp);

}

