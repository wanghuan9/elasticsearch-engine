package com.elasticsearch.engine.elasticsearchengine.mapping.model;

import com.elasticsearch.engine.elasticsearchengine.model.domain.QueryBean;
import org.elasticsearch.index.query.TermQueryBuilder;

/**
 * @author wanghuan
 * @description: TermQueryBean
 * @date 2022-01-26 11:28
 */
public class TermQueryBean extends QueryBean<TermQueryBuilder> {

    @Override
    public void configQueryBuilder(TermQueryBuilder queryBuilder) {
    }
}
