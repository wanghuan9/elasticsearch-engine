package com.elasticsearch.engine.elasticsearchengine.execute.querymodel;

import com.elasticsearch.engine.elasticsearchengine.mapping.annotation.Aggs;
import com.elasticsearch.engine.elasticsearchengine.model.annotion.EsQueryIndex;
import lombok.Data;

/**
 * @author wanghuan
 * @description: ROOD
 * @date 2022-03-24 16:58
 */
@EsQueryIndex(value = "supplier_item_spare")
@Data
public class SupplierItemAggs {

    @Aggs(type = Aggs.COUNT_DESC)
    private Integer status;
}
