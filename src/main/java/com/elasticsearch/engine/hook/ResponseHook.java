package com.elasticsearch.engine.hook;

import org.elasticsearch.action.search.SearchResponse;

/**
 * author     JohenTeng
 * date      2021/7/21
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

