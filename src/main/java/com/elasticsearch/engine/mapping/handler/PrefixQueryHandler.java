package com.elasticsearch.engine.mapping.handler;

import com.elasticsearch.engine.holder.AbstractEsRequestHolder;
import com.elasticsearch.engine.mapping.annotation.Prefix;
import com.elasticsearch.engine.mapping.model.PrefixQueryBean;
import com.elasticsearch.engine.model.annotion.EsQueryHandle;
import com.elasticsearch.engine.model.domain.EsQueryFieldBean;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;

/**
 * @author wanghuan
 * @description: PrefixQueryHandler
 * @date 2022-01-26 11:28
 */
@EsQueryHandle(Prefix.class)
public class PrefixQueryHandler extends AbstractQueryHandler<PrefixQueryBean> {

    @Override
    public QueryBuilder handle(EsQueryFieldBean<PrefixQueryBean> queryDes, AbstractEsRequestHolder searchHelper) {
        String value = queryDes.getValue().toString();
        QueryBuilder prefixQueryBuilder = QueryBuilders.prefixQuery(queryDes.getField(), value);
        searchHelper.chain(prefixQueryBuilder);
        return prefixQueryBuilder;
    }
}