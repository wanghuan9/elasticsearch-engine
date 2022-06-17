package com.elasticsearch.engine.base.mapping.handler;

import com.elasticsearch.engine.base.holder.AbstractEsRequestHolder;
import com.elasticsearch.engine.base.mapping.annotation.PageAndOrder;
import com.elasticsearch.engine.base.mapping.model.PageQueryBean;
import com.elasticsearch.engine.base.mapping.model.extend.PageParam;
import com.elasticsearch.engine.base.model.annotion.EsQueryHandle;
import com.elasticsearch.engine.base.model.domain.EsQueryFieldBean;
import com.elasticsearch.engine.base.model.exception.EsEngineQueryException;
import org.apache.commons.collections4.MapUtils;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.SortOrder;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @author wanghuan
 * @description: PageAndOrderQueryHandler
 * @date 2022-01-26 11:28
 */
@EsQueryHandle(PageAndOrder.class)
public class PageAndOrderQueryHandler extends AbstractQueryHandler<PageQueryBean> {

    @Override
    public QueryBuilder handle(EsQueryFieldBean queryDes, AbstractEsRequestHolder searchHelper) {
        Object value = queryDes.getValue();
        if (!value.getClass().equals(PageParam.class)) {
            throw new EsEngineQueryException("@PageAndOrder have to define as PageParam.class");
        }
        PageParam pageParam = (PageParam) value;
        SearchSourceBuilder source = searchHelper.getSource();
        source.from(pageParam.getExclude()).size(pageParam.getPageSize());
        LinkedHashMap<String, SortOrder> orderMap = pageParam.getOrderMap();
        if (MapUtils.isNotEmpty(orderMap)) {
            for (Map.Entry<String, SortOrder> entry : orderMap.entrySet()) {
                source.sort(entry.getKey(), Objects.nonNull(entry.getValue()) ? entry.getValue() : SortOrder.ASC);
            }
        }
        return null;
    }
}
