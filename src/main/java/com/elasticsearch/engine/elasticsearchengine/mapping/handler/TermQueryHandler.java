package com.elasticsearch.engine.elasticsearchengine.mapping.handler;

import com.elasticsearch.engine.elasticsearchengine.holder.AbstractEsRequestHolder;
import com.elasticsearch.engine.elasticsearchengine.mapping.annotation.Term;
import com.elasticsearch.engine.elasticsearchengine.mapping.model.TermQueryBean;
import com.elasticsearch.engine.elasticsearchengine.model.annotion.EsQueryHandle;
import com.elasticsearch.engine.elasticsearchengine.model.domain.EsQueryFieldBean;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.TermQueryBuilder;

/**
 * TermQueryHandle
 *
 * @author wanghuan
 * @EsQueryIndex(index = "test_index"， model = QueryModel.bool)
 * class Param {
 * @EsQueryField(name = "user_name", logicConnector = EsConnector.SHOULD, meta = EsMeta.KEYWORD, boost = 2.0)
 * private String userName;
 * <p>
 * }
 * @date 2022-01-26 11:28
 */
@EsQueryHandle(Term.class)
public class TermQueryHandler extends AbstractQueryHandler<TermQueryBean> {

    /**
     * @param queryDes
     * @param searchHelper return
     */
    @Override
    public QueryBuilder handle(EsQueryFieldBean queryDes, AbstractEsRequestHolder searchHelper) {
        TermQueryBuilder termQueryBuilder = QueryBuilders.termQuery(queryDes.getField(), queryDes.getValue());
        searchHelper.chain(termQueryBuilder);
        return termQueryBuilder;
    }
}
