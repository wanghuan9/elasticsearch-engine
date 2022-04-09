package com.elasticsearch.engine.elasticsearchengine.mapping.model;

import com.elasticsearch.engine.elasticsearchengine.model.domain.QueryBean;
import lombok.Data;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.search.sort.SortOrder;

/**
 * @author wanghuan
 * @description: SortQueryBean
 * @date 2022-01-26 11:28
 */
@Data
public class SortQueryBean extends QueryBean {

    private SortOrder type;

    private int order;


    @Override
    public void configQueryBuilder(QueryBuilder queryBuilder) {

    }

}
