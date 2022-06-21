package com.elasticsearch.engine.base.mapping.handler;

import com.elasticsearch.engine.base.holder.AbstractEsRequestHolder;
import com.elasticsearch.engine.base.mapping.annotation.Sort;
import com.elasticsearch.engine.base.mapping.model.SortQueryBean;
import com.elasticsearch.engine.base.model.annotion.EsQueryHandle;
import com.elasticsearch.engine.base.model.domain.EsQueryFieldBean;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;

/**
 * @author wanghuan
 * @description: SortQueryHandler
 * @date 2022-01-26 11:28
 */
@EsQueryHandle(Sort.class)
public class SortQueryHandler extends AbstractQueryHandler<SortQueryBean> {

    @Override
    public QueryBuilder handle(EsQueryFieldBean<SortQueryBean> queryDes, AbstractEsRequestHolder searchHelper) {
        SearchSourceBuilder source = searchHelper.getSource();
        SortQueryBean extBean = queryDes.getExtBean();
        source.sort(queryDes.getField(), extBean.getType());
        return null;
    }

}
