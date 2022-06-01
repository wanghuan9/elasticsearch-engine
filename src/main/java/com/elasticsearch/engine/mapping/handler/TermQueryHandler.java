package com.elasticsearch.engine.mapping.handler;

import com.elasticsearch.engine.holder.AbstractEsRequestHolder;
import com.elasticsearch.engine.mapping.annotation.Term;
import com.elasticsearch.engine.mapping.model.TermQueryBean;
import com.elasticsearch.engine.model.annotion.EsQueryHandle;
import com.elasticsearch.engine.model.domain.EsQueryFieldBean;
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
