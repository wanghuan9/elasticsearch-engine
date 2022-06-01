package com.elasticsearch.engine.execute.querymodel;

import com.elasticsearch.engine.mapping.annotation.Term;
import com.elasticsearch.engine.model.annotion.Base;
import com.elasticsearch.engine.model.annotion.EsQueryIndex;
import com.elasticsearch.engine.model.emenu.EsConnector;
import lombok.Data;

@EsQueryIndex(value = "supplier_item_spare")
@Data
public class SupplierItemShould {

    @Term(value = @Base(connect = EsConnector.SHOULD))
    private String itemNo;

    @Term(value = @Base(connect = EsConnector.SHOULD))
    private Integer status;

    @Term(value = @Base(connect = EsConnector.SHOULD))
    private String productName;


}

