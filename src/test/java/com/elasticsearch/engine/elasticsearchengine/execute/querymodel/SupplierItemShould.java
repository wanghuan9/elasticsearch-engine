package com.elasticsearch.engine.elasticsearchengine.execute.querymodel;

import com.elasticsearch.engine.elasticsearchengine.mapping.annotation.Term;
import com.elasticsearch.engine.elasticsearchengine.model.annotion.Base;
import com.elasticsearch.engine.elasticsearchengine.model.annotion.EsQueryIndex;
import com.elasticsearch.engine.elasticsearchengine.model.emenu.EsConnector;
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

