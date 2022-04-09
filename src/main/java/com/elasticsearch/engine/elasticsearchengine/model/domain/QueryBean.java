package com.elasticsearch.engine.elasticsearchengine.model.domain;

/**
 * @author wanghuan
 * @description: Define base-query-information
 * @date 2022-01-26 11:28
 */

import org.elasticsearch.index.query.QueryBuilder;

public abstract class QueryBean<T extends QueryBuilder> {


    /**
     * use this extend-config-bean to config given @QueryBuilder
     *
     * @param queryBuilder (ex: MatchQueryBuilder, MultiMatchQuery ...)
     */
    public abstract void configQueryBuilder(T queryBuilder);


}
