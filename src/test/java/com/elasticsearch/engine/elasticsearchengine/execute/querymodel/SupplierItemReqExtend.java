package com.elasticsearch.engine.elasticsearchengine.execute.querymodel;

import com.elasticsearch.engine.elasticsearchengine.holder.AbstractEsRequestHolder;
import com.elasticsearch.engine.elasticsearchengine.hook.RequestHook;
import com.elasticsearch.engine.elasticsearchengine.mapping.annotation.Term;
import com.elasticsearch.engine.elasticsearchengine.model.annotion.EsQueryIndex;
import com.elasticsearch.engine.elasticsearchengine.model.annotion.Ignore;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.TermsQueryBuilder;

import java.util.List;

@Slf4j
@EsQueryIndex(value = "supplier_item")
@Data
public class SupplierItemReqExtend implements RequestHook<SupplierItemReqExtend> {

    @Term
    private Integer status;

    @Ignore
    private List<String> itemNoList;

    /**
     * user define the operation of request
     * you can extend-define Es-request or
     * define aggregation
     *
     * @param holder
     * @param supplierItem
     */
    @Override
    public AbstractEsRequestHolder handleRequest(AbstractEsRequestHolder holder, SupplierItemReqExtend supplierItem) {
        TermsQueryBuilder queryBuilder = QueryBuilders.termsQuery("item_no", supplierItem.getItemNoList());
        holder.addQueryBuilder(queryBuilder);
        return holder;
    }
}

