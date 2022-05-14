package com.elasticsearch.engine.elasticsearchengine.execute.querymodel;

import com.elasticsearch.engine.elasticsearchengine.mapping.annotation.Sort;
import com.elasticsearch.engine.elasticsearchengine.mapping.model.extend.SignParam;
import com.elasticsearch.engine.elasticsearchengine.model.annotion.EsQueryIndex;
import lombok.Data;
import org.elasticsearch.search.sort.SortOrder;

@EsQueryIndex(value = "supplier_item_spare")
@Data
public class SupplierItemSort {

    @Sort(type = SortOrder.DESC)
    private SignParam status;

    private String productName;

}

