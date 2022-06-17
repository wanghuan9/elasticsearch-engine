package com.elasticsearch.engine.mapping.model;

import com.elasticsearch.engine.model.domain.AbstractQueryBean;
import com.elasticsearch.engine.model.domain.EsQueryFieldBean;
import org.elasticsearch.index.query.TermQueryBuilder;

/**
 * @author wanghuan
 * @description: TermQueryBean
 * @date 2022-01-26 11:28
 */
public class TermQueryBean extends AbstractQueryBean<TermQueryBuilder> {

    @Override
    public void configQueryBuilder(EsQueryFieldBean queryDes, TermQueryBuilder queryBuilder) {
    }
}
