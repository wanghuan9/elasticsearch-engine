package com.elasticsearch.engine.elasticsearchengine.execute.querymodel;

import com.elasticsearch.engine.elasticsearchengine.mapping.annotation.Exist;
import com.elasticsearch.engine.elasticsearchengine.model.annotion.Base;
import com.elasticsearch.engine.elasticsearchengine.model.annotion.EsQueryIndex;
import com.elasticsearch.engine.elasticsearchengine.model.emenu.EsConnector;
import lombok.Data;

@EsQueryIndex(index = "supplier_item")
@Data
public class SupplierItemExist {

    // productName!=null
    @Exist
    private String productName;

    //status==null
    @Exist(@Base(connect = EsConnector.MUST_NOT))
    private String status;
}

