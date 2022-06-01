package com.elasticsearch.engine.execute.querymodel;

import com.elasticsearch.engine.mapping.annotation.Exist;
import com.elasticsearch.engine.model.annotion.Base;
import com.elasticsearch.engine.model.annotion.EsQueryIndex;
import com.elasticsearch.engine.model.emenu.EsConnector;
import lombok.Data;

@EsQueryIndex(value = "supplier_item")
@Data
public class SupplierItemExist {

    // productName!=null
    @Exist
    private String productName;

    //status==null
    @Exist(@Base(connect = EsConnector.MUST_NOT))
    private String status;
}

