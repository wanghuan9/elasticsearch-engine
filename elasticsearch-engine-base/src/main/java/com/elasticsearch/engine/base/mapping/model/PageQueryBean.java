package com.elasticsearch.engine.base.mapping.model;

import com.elasticsearch.engine.base.model.domain.AbstractQueryBean;
import com.elasticsearch.engine.base.model.domain.EsQueryFieldBean;
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
    public void configQueryBuilder(EsQueryFieldBean queryDes, QueryBuilder queryBuilder) {

    }

}
