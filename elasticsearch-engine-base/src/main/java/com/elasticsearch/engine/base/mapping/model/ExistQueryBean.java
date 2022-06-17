package com.elasticsearch.engine.base.mapping.model;

import com.elasticsearch.engine.base.model.domain.AbstractQueryBean;
import com.elasticsearch.engine.base.model.domain.EsQueryFieldBean;
import org.elasticsearch.index.query.ExistsQueryBuilder;

/**
 * @author wanghuan
 * @description: ExistQueryBean
 * @date 2022-02-11 17:38
 */
public class ExistQueryBean extends AbstractQueryBean<ExistsQueryBuilder> {

    /**
     * use this extend-config-bean to config given @QueryBuilder
     *
     * @param queryBuilder (ex: MatchQueryBuilder, MultiMatchQuery ...)
     */
    @Override
    public void configQueryBuilder(EsQueryFieldBean queryDes, ExistsQueryBuilder queryBuilder) {

    }
}
