package com.elasticsearch.engine.execute.querymodel;

import com.elasticsearch.engine.holder.AbstractEsRequestHolder;
import com.elasticsearch.engine.hook.RequestHook;
import com.elasticsearch.engine.model.annotion.EsQueryIndex;
import com.elasticsearch.engine.model.annotion.Ignore;
import com.elasticsearch.engine.model.annotion.Nested;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.TermsQueryBuilder;

@Slf4j
@EsQueryIndex(value = "supplier_item")
@Data
public class SupplierItemNestedExtend implements RequestHook<SupplierItemNestedExtend> {

    @Nested
    private SupplierItemReqExtend supplierItemReqExtend;

    @Ignore
    private String productName;

    /**
     * user define the operation of request
     * you can extend-define Es-request or
     * define aggregation
     *
     * @param holder
     * @param supplierItem
     */
    @Override
    public AbstractEsRequestHolder handleRequest(AbstractEsRequestHolder holder, SupplierItemNestedExtend supplierItem) {
        TermsQueryBuilder queryBuilder = QueryBuilders.termsQuery("product_name", supplierItem.getProductName());
        holder.addQueryBuilder(queryBuilder);
        return holder;
    }
}

