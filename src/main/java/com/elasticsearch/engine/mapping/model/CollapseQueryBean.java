package com.elasticsearch.engine.mapping.model;

import com.elasticsearch.engine.model.domain.AbstractQueryBean;
import com.elasticsearch.engine.model.domain.EsQueryFieldBean;
import lombok.Data;
import org.elasticsearch.index.query.QueryBuilder;

/**
 * @author wanghuan
 * @description: CollapseQueryBean
 * @date 2022-01-26 11:28
 */
@Data
public class CollapseQueryBean extends AbstractQueryBean {

    private int size;

    @Override
    public void configQueryBuilder(EsQueryFieldBean queryDes, QueryBuilder queryBuilder) {

    }

}
