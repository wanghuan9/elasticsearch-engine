package com.elasticsearch.engine.elasticsearchengine.mapping.handler;

import com.elasticsearch.engine.elasticsearchengine.holder.AbstractEsRequestHolder;
import com.elasticsearch.engine.elasticsearchengine.mapping.annotation.Collapse;
import com.elasticsearch.engine.elasticsearchengine.mapping.model.CollapseQueryBean;
import com.elasticsearch.engine.elasticsearchengine.model.annotion.EsQueryHandle;
import com.elasticsearch.engine.elasticsearchengine.model.domain.EsQueryFieldBean;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.collapse.CollapseBuilder;

/**
 * @author wanghuan
 * @description: CollapseQueryHandler
 * @date 2022-01-26 11:28
 */
@EsQueryHandle(Collapse.class)
public class CollapseQueryHandler extends AbstractQueryHandler<CollapseQueryBean> {

    @Override
    public QueryBuilder handle(EsQueryFieldBean<CollapseQueryBean> queryDes, AbstractEsRequestHolder searchHelper) {
        SearchSourceBuilder source = searchHelper.getSource();
        CollapseQueryBean extBean = queryDes.getExtBean();
        source.collapse(new CollapseBuilder(queryDes.getField()))
                .size(extBean.getSize());
        return null;
    }

}
