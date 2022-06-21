package com.elasticsearch.engine.base.model.domain;

/**
 * @author wanghuan
 * @description: Define base-query-information
 * @date 2022-01-26 11:28
 */

import org.elasticsearch.index.query.QueryBuilder;

public abstract class AbstractQueryBean<T extends QueryBuilder> {


    /**
     * use this extend-config-bean to config given @QueryBuilder
     *
     * @param queryBuilder (ex: MatchQueryBuilder, MultiMatchQuery ...)
     */
    public abstract void configQueryBuilder(EsQueryFieldBean queryDes, T queryBuilder);


}
