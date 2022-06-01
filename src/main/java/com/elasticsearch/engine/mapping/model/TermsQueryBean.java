package com.elasticsearch.engine.mapping.model;

import com.elasticsearch.engine.model.domain.AbstractQueryBean;
import org.elasticsearch.index.query.TermsQueryBuilder;

/**
 * @author wanghuan
 * @description: TermsQueryBean
 * @date 2022-01-26 11:28
 */
public class TermsQueryBean extends AbstractQueryBean<TermsQueryBuilder> {

    @Override
    public void configQueryBuilder(TermsQueryBuilder queryBuilder) {
    }
}
