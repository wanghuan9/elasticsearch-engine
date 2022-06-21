package com.elasticsearch.engine.base.mapping.handler;

import com.elasticsearch.engine.base.holder.AbstractEsRequestHolder;
import com.elasticsearch.engine.base.mapping.annotation.Prefix;
import com.elasticsearch.engine.base.mapping.model.PrefixQueryBean;
import com.elasticsearch.engine.base.model.annotion.EsQueryHandle;
import com.elasticsearch.engine.base.model.domain.EsQueryFieldBean;
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
