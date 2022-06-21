package com.elasticsearch.engine.base.mapping.model;

import com.elasticsearch.engine.base.model.domain.AbstractQueryBean;
import com.elasticsearch.engine.base.model.domain.EsQueryFieldBean;
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
