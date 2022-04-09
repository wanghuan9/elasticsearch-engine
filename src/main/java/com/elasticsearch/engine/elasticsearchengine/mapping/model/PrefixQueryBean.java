package com.elasticsearch.engine.elasticsearchengine.mapping.model;

import com.elasticsearch.engine.elasticsearchengine.model.domain.QueryBean;
import org.elasticsearch.index.query.PrefixQueryBuilder;

/**
 * @author wanghuan
 * @description: PrefixQueryBean
 * @date 2022-01-26 11:28
 */
public class PrefixQueryBean extends QueryBean<PrefixQueryBuilder> {

    @Override
    public void configQueryBuilder(PrefixQueryBuilder queryBuilder) {
    }
}
