package com.elasticsearch.engine.elasticsearchengine.mapping.handler;

import com.elasticsearch.engine.elasticsearchengine.holder.AbstractEsRequestHolder;
import com.elasticsearch.engine.elasticsearchengine.mapping.annotation.WildCard;
import com.elasticsearch.engine.elasticsearchengine.mapping.model.WildCardQueryBean;
import com.elasticsearch.engine.elasticsearchengine.model.annotion.EsQueryHandle;
import com.elasticsearch.engine.elasticsearchengine.model.domain.EsQueryFieldBean;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.WildcardQueryBuilder;

/**
 * @author wanghuan
 * @description: WildCardQueryHandler
 * @date 2022-01-26 11:28
 */
@EsQueryHandle(WildCard.class)
public class WildCardQueryHandler extends AbstractQueryHandler<WildCardQueryBean> {

    @Override
    public QueryBuilder handle(EsQueryFieldBean<WildCardQueryBean> queryDes, AbstractEsRequestHolder searchHelper) {
        String value = queryDes.getValue().toString();
        WildCardQueryBean wildCardQueryBean = queryDes.getExtBean();
        String tag = wildCardQueryBean.getTag();
        WildcardQueryBuilder queryBuilder = QueryBuilders.wildcardQuery(queryDes.getField(), String.format(tag, value));
        searchHelper.chain(queryBuilder);
        return queryBuilder;
    }
}
