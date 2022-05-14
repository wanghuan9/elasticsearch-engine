package com.elasticsearch.engine.elasticsearchengine.execute.querymodel;

import com.elasticsearch.engine.elasticsearchengine.model.annotion.EsQueryIndex;
import com.elasticsearch.engine.elasticsearchengine.model.annotion.Ignore;
import lombok.Data;

import java.util.List;

@EsQueryIndex(value = "supplier_item_spare")
@Data
public class SupplierItemIgnore {

    private List<String> itemNo;

    @Ignore
    private String productName;

}

