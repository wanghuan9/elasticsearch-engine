package com.elasticsearch.engine.elasticsearchengine.mapping.handler;

import com.elasticsearch.engine.elasticsearchengine.holder.AbstractEsRequestHolder;
import com.elasticsearch.engine.elasticsearchengine.mapping.annotation.Range;
import com.elasticsearch.engine.elasticsearchengine.mapping.model.RangeQueryBean;
import com.elasticsearch.engine.elasticsearchengine.mapping.model.extend.RangeParam;
import com.elasticsearch.engine.elasticsearchengine.model.annotion.EsQueryHandle;
import com.elasticsearch.engine.elasticsearchengine.model.domain.EsQueryFieldBean;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;

import java.util.Objects;
import java.util.Optional;

/**
 * @author wanghuan
 * @description: RangeQueryHandler
 * @date 2022-01-26 11:28
 */
@EsQueryHandle(Range.class)
public class RangeQueryHandler extends AbstractQueryHandler<RangeQueryBean> {

    @Override
    public QueryBuilder handle(EsQueryFieldBean<RangeQueryBean> queryDes, AbstractEsRequestHolder searchHelper) {
        RangeParam rangeParam = (RangeParam) queryDes.getValue();
        if (Objects.isNull(rangeParam.getLeft()) && Objects.isNull(rangeParam.getRight())) {
            return null;
        }
        final RangeQueryBuilder rangeQuery = QueryBuilders.rangeQuery(queryDes.getField());
        RangeQueryBean rangeBean = queryDes.getExtBean();
        switch (rangeBean.getTag()) {
            case Range.LE_GE:
                Optional.ofNullable(rangeParam.getLeft()).ifPresent(rangeQuery::gte);
                Optional.ofNullable(rangeParam.getRight()).ifPresent(rangeQuery::lte);
                break;
            case Range.L_G:
                Optional.ofNullable(rangeParam.getLeft()).ifPresent(rangeQuery::gt);
                Optional.ofNullable(rangeParam.getRight()).ifPresent(rangeQuery::lt);
                break;
            case Range.LE_G:
                Optional.ofNullable(rangeParam.getLeft()).ifPresent(rangeQuery::gte);
                Optional.ofNullable(rangeParam.getRight()).ifPresent(rangeQuery::lt);
                break;
            case Range.L_GE:
                Optional.ofNullable(rangeParam.getLeft()).ifPresent(rangeQuery::gt);
                Optional.ofNullable(rangeParam.getRight()).ifPresent(rangeQuery::lte);
                break;
            case Range.F_T:
                Optional.ofNullable(rangeParam.getLeft()).ifPresent(rangeQuery::from);
                Optional.ofNullable(rangeParam.getRight()).ifPresent(rangeQuery::to);
                break;
            default:
        }
        searchHelper.chain(rangeQuery);
        return rangeQuery;
    }

}
