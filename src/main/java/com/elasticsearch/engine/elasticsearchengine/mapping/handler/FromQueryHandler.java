package com.elasticsearch.engine.elasticsearchengine.mapping.handler;

import com.elasticsearch.engine.elasticsearchengine.holder.AbstractEsRequestHolder;
import com.elasticsearch.engine.elasticsearchengine.mapping.annotation.From;
import com.elasticsearch.engine.elasticsearchengine.mapping.model.FromQueryBean;
import com.elasticsearch.engine.elasticsearchengine.model.annotion.EsQueryHandle;
import com.elasticsearch.engine.elasticsearchengine.model.domain.EsQueryFieldBean;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;

import java.util.Map;

/**
 * @author wanghuan
 * @description: from 查询Handler
 * @date 2022-02-07 10:11
 */
@EsQueryHandle(From.class)
public class FromQueryHandler extends AbstractQueryHandler<FromQueryBean> {


    /**
     * 查询注解解析拼接查询语句
     *
     * @param queryDes
     * @param searchHelper
     */
    @Override
    public QueryBuilder handle(EsQueryFieldBean<FromQueryBean> queryDes, AbstractEsRequestHolder searchHelper) {
        Map<Integer, RangeQueryBuilder> map = searchHelper.getRange();
        FromQueryBean fromQueryBean = queryDes.getExtBean();
        Integer group = fromQueryBean.getGroup();
        RangeQueryBuilder range;
        if (map.containsKey(group)) {
            range = map.get(group);
        } else {
            range = QueryBuilders.rangeQuery(queryDes.getField());
        }
        range.from(queryDes.getValue());
        map.put(group, range);
        return range;
    }
}
