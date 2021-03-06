package com.elasticsearch.engine.base.mapping.model;

import com.elasticsearch.engine.base.model.domain.AbstractQueryBean;
import com.elasticsearch.engine.base.model.domain.EsQueryFieldBean;
import lombok.Data;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.search.sort.SortOrder;

/**
 * @author wanghuan
 * @description: SortQueryBean
 * @date 2022-01-26 11:28
 */
@Data
public class SortQueryBean extends AbstractQueryBean {

    private SortOrder type;

    private int order;


    @Override
    public void configQueryBuilder(EsQueryFieldBean queryDes, QueryBuilder queryBuilder) {

    }

}
