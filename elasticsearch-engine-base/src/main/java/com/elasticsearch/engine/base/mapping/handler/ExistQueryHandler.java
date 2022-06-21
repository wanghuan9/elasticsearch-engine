package com.elasticsearch.engine.base.mapping.handler;

import com.elasticsearch.engine.base.holder.AbstractEsRequestHolder;
import com.elasticsearch.engine.base.mapping.annotation.Exist;
import com.elasticsearch.engine.base.mapping.model.ExistQueryBean;
import com.elasticsearch.engine.base.model.annotion.EsQueryHandle;
import com.elasticsearch.engine.base.model.domain.EsQueryFieldBean;
import org.elasticsearch.index.query.ExistsQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;

/**
 * @author wanghuan
 * @description: ExistQueryHandler
 * @date 2022-02-11 17:40
 */
@EsQueryHandle(Exist.class)
public class ExistQueryHandler extends AbstractQueryHandler<ExistQueryBean> {
    /**
     * 查询注解解析拼接查询语句
     *
     * @param queryDes
     * @param searchHelper
     */
    @Override
    public QueryBuilder handle(EsQueryFieldBean<ExistQueryBean> queryDes, AbstractEsRequestHolder searchHelper) {
        ExistsQueryBuilder existsQueryBuilder = QueryBuilders.existsQuery(queryDes.getField());
        searchHelper.chain(existsQueryBuilder);
        return existsQueryBuilder;
    }
}
