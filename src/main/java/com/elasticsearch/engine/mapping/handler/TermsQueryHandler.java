package com.elasticsearch.engine.mapping.handler;

import com.elasticsearch.engine.common.utils.ReflectionUtils;
import com.elasticsearch.engine.holder.AbstractEsRequestHolder;
import com.elasticsearch.engine.mapping.annotation.Terms;
import com.elasticsearch.engine.mapping.model.TermsQueryBean;
import com.elasticsearch.engine.model.annotion.EsQueryHandle;
import com.elasticsearch.engine.model.domain.EsQueryFieldBean;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.TermsQueryBuilder;

import java.util.Collection;

/**
 * @author wanghuan
 * @description: TermsQueryHandler
 * @date 2022-01-26 11:28
 */
@EsQueryHandle(Terms.class)
public class TermsQueryHandler extends AbstractQueryHandler<TermsQueryBean> {

    @Override
    public QueryBuilder handle(EsQueryFieldBean<TermsQueryBean> queryDes, AbstractEsRequestHolder searchHelper) {
        Collection value = ReflectionUtils.transArrayOrCollection(queryDes.getValue());
        TermsQueryBuilder queryBuilder = QueryBuilders.termsQuery(queryDes.getField(), value);
        searchHelper.chain(queryBuilder);
        return queryBuilder;
    }

}
