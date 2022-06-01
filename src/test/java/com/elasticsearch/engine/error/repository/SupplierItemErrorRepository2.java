package com.elasticsearch.engine.error.repository;

import com.elasticsearch.engine.execute.resultmodel.SupplierItemEntity;
import com.elasticsearch.engine.mapping.annotation.Collapse;
import com.elasticsearch.engine.mapping.annotation.Term;
import com.elasticsearch.engine.model.domain.BaseESRepository;

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
