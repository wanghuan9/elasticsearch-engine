package com.elasticsearch.engine.execute.querymodel;

import com.elasticsearch.engine.mapping.annotation.Sort;
import com.elasticsearch.engine.mapping.model.extend.SignParam;
import com.elasticsearch.engine.model.annotion.Base;
import com.elasticsearch.engine.model.annotion.EsQueryIndex;
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

