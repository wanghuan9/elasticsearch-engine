package com.elasticsearch.engine.elasticsearchengine.error.repository;

import com.elasticsearch.engine.elasticsearchengine.execute.querymodel.SupplierItem;
import com.elasticsearch.engine.elasticsearchengine.execute.resultmodel.SupplierItemEntity;
import com.elasticsearch.engine.elasticsearchengine.model.annotion.EsQueryIndex;
import com.elasticsearch.engine.elasticsearchengine.model.domain.BaseESRepository;

/**
 * 异常场景测试  出入参不符合标准
 */
@EsQueryIndex(value = "supplier_item_spare")
public interface SupplierItemParamErrorRepository extends BaseESRepository<SupplierItemEntity, String> {

    /**
     * 入参异常测试
     *
     * @param param
     * @return
     */
    SupplierItemEntity queryOne(SupplierItem param, SupplierItemEntity item);

    /**
     * 出参异常测试
     *
     * @param param
     * @return
     */
    SupplierItem queryOne(SupplierItem param);
}
