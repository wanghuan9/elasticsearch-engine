package com.elasticsearch.engine.elasticsearchengine.mapping.model;

import com.elasticsearch.engine.elasticsearchengine.model.domain.QueryBean;
import lombok.Data;
import org.elasticsearch.index.query.QueryBuilder;

/**
 * @author wanghuan
 * @description: CollapseQueryBean
 * @date 2022-01-26 11:28
 */
@Data
public class CollapseQueryBean extends QueryBean {

    private int size;

    @Override
    public void configQueryBuilder(QueryBuilder queryBuilder) {

    }

}
