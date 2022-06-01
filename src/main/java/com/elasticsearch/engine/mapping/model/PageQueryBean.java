package com.elasticsearch.engine.mapping.model;

import com.elasticsearch.engine.model.domain.AbstractQueryBean;
import lombok.Data;
import org.elasticsearch.index.query.QueryBuilder;

/**
 * @author wanghuan
 * @description: PageQueryBean
 * @date 2022-01-26 11:28
 */
@Data
public class PageQueryBean extends AbstractQueryBean {

    @Override
    public void configQueryBuilder(QueryBuilder queryBuilder) {

    }

}
