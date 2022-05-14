package com.elasticsearch.engine.elasticsearchengine.execute.querymodel;

import com.elasticsearch.engine.elasticsearchengine.mapping.annotation.Sort;
import com.elasticsearch.engine.elasticsearchengine.mapping.model.extend.SignParam;
import com.elasticsearch.engine.elasticsearchengine.model.annotion.Base;
import com.elasticsearch.engine.elasticsearchengine.model.annotion.EsQueryIndex;
import lombok.Data;
import org.elasticsearch.search.sort.SortOrder;

@EsQueryIndex(value = "supplier_item_spare")
@Data
public class SupplierItemSortOrder {

    @Sort(value = @Base(order = 5), type = SortOrder.DESC)
    private SignParam status;

    @Sort(value = @Base(order = 4), type = SortOrder.DESC)
    private String productName;

}

