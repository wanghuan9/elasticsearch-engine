package com.elasticsearch.engine.elasticsearchengine.execute.querymodel;

import com.elasticsearch.engine.elasticsearchengine.mapping.annotation.*;
import com.elasticsearch.engine.elasticsearchengine.mapping.model.extend.PageParam;
import com.elasticsearch.engine.elasticsearchengine.mapping.model.extend.RangeParam;
import com.elasticsearch.engine.elasticsearchengine.model.annotion.Base;
import com.elasticsearch.engine.elasticsearchengine.model.annotion.EsQueryIndex;
import com.elasticsearch.engine.elasticsearchengine.model.emenu.EsConnector;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@EsQueryIndex(value = "supplier_item_spare")
@Data
public class SupplierItem {

    @Terms
    private List<String> itemNo;

    private List<String> itemNoList;

    @Term(value = @Base())
    private BigDecimal warehousePrice;

    @Range(value = @Base(connect = EsConnector.SHOULD), tag = Range.LE_GE)
    private RangeParam status;

    @Range
    private RangeParam createDt;

    @WildCard
    private String productName;

    @Prefix
    private String skuName;

    @To
    private LocalDateTime endCreateDt;

    @From
    private LocalDateTime createDtStart;

    @PageAndOrder
    private PageParam pageParam;

}

