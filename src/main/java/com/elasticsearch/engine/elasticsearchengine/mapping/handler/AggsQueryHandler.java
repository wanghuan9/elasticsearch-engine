package com.elasticsearch.engine.elasticsearchengine.mapping.handler;

import com.elasticsearch.engine.elasticsearchengine.holder.AbstractEsRequestHolder;
import com.elasticsearch.engine.elasticsearchengine.mapping.annotation.Aggs;
import com.elasticsearch.engine.elasticsearchengine.mapping.model.AggsQueryBean;
import com.elasticsearch.engine.elasticsearchengine.model.annotion.EsQueryHandle;
import com.elasticsearch.engine.elasticsearchengine.model.constant.EsConstant;
import com.elasticsearch.engine.elasticsearchengine.model.domain.EsQueryFieldBean;
import org.apache.commons.lang3.math.NumberUtils;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.BucketOrder;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;

/**
 * @author wanghuan
 * @description: AggsQueryHandler
 * @date 2022-01-26 11:28
 */
@EsQueryHandle(Aggs.class)
public class AggsQueryHandler extends AbstractQueryHandler<AggsQueryBean> {

    @Override
    public QueryBuilder handle(EsQueryFieldBean<AggsQueryBean> queryDes, AbstractEsRequestHolder searchHelper) {
        SearchSourceBuilder source = searchHelper.getSource();
        AggsQueryBean extBean = queryDes.getExtBean();
        TermsAggregationBuilder aggs = AggregationBuilders
                .terms(EsConstant.AGG)
                .field(queryDes.getField())
                .size(extBean.getSize());
        source.aggregation(aggs)
                .size(NumberUtils.INTEGER_ZERO);
        //设置排序
        switch (extBean.getType()) {
            case Aggs.KEY_ASC:
                //按照key排序  true 为 asc
                aggs.order(BucketOrder.key(true));
                break;
            case Aggs.KEY_DESC:
                aggs.order(BucketOrder.key(false));
                break;
            case Aggs.COUNT_ASC:
                aggs.order(BucketOrder.count(true));
                break;
            case Aggs.COUNT_DESC:
                aggs.order(BucketOrder.count(false));
                break;
            default:
        }
        return null;
    }

}
