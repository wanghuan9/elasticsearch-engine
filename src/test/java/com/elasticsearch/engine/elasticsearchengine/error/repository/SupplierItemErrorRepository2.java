package com.elasticsearch.engine.elasticsearchengine.error.repository;

import com.elasticsearch.engine.elasticsearchengine.execute.resultmodel.SupplierItemEntity;
import com.elasticsearch.engine.elasticsearchengine.mapping.annotation.Collapse;
import com.elasticsearch.engine.elasticsearchengine.mapping.annotation.Term;
import com.elasticsearch.engine.elasticsearchengine.model.domain.BaseESRepository;

/**
 * 异常场景测试  未添加EsQueryIndex
 */
public interface SupplierItemErrorRepository2 extends BaseESRepository<SupplierItemEntity, String> {

    /**
     * 查询单个
     *
     * @param itemNo
     * @return
     */
    SupplierItemEntity queryOne(@Term String itemNo, @Collapse Integer status);


}
