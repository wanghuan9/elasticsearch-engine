package com.elasticsearch.engine.elasticsearchengine.mapping.model;

import com.elasticsearch.engine.elasticsearchengine.model.domain.QueryBean;
import lombok.Data;
import org.elasticsearch.index.query.QueryBuilder;

/**
 * @author wanghuan
 * @description: AggsQueryBean
 * @date 2022-01-26 11:28
 */
@Data
public class AggsQueryBean extends QueryBean {

    private String type;

    private int size;

    @Override
    public void configQueryBuilder(QueryBuilder queryBuilder) {

    }

}
