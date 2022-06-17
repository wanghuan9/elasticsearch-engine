package com.elasticsearch.engine.base.mapping.handler;

import com.elasticsearch.engine.base.holder.AbstractEsRequestHolder;
import com.elasticsearch.engine.base.mapping.annotation.To;
import com.elasticsearch.engine.base.mapping.model.ToQueryBean;
import com.elasticsearch.engine.base.model.annotion.EsQueryHandle;
import com.elasticsearch.engine.base.model.domain.EsQueryFieldBean;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;

import java.util.Map;

/**
 * @author wanghuan
 * @description: from 查询Handler
 * @date 2022-02-07 10:11
 */
@EsQueryHandle(To.class)
public class ToQueryHandler extends AbstractQueryHandler<ToQueryBean> {


    /**
     * 查询注解解析拼接查询语句
     *
     * @param queryDes
     * @param searchHelper
     */
    @Override
    public QueryBuilder handle(EsQueryFieldBean<ToQueryBean> queryDes, AbstractEsRequestHolder searchHelper) {
        Map<Integer, RangeQueryBuilder> map = searchHelper.getRange();
        ToQueryBean toQueryBean = queryDes.getExtBean();
        Integer group = toQueryBean.getGroup();
        RangeQueryBuilder range;
        if (map.containsKey(group)) {
            range = map.get(group);
        } else {
            range = QueryBuilders.rangeQuery(queryDes.getField());
        }
        map.put(group, range);
        range.to(queryDes.getValue());
        return range;
    }
}
