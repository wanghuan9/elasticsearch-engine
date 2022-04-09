package com.elasticsearch.engine.elasticsearchengine.mapping.model;

import com.elasticsearch.engine.elasticsearchengine.model.domain.QueryBean;
import org.elasticsearch.index.query.TermsQueryBuilder;

/**
 * @author wanghuan
 * @description: TermsQueryBean
 * @date 2022-01-26 11:28
 */
public class TermsQueryBean extends QueryBean<TermsQueryBuilder> {

    @Override
    public void configQueryBuilder(TermsQueryBuilder queryBuilder) {
    }
}
