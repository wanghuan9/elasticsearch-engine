package com.elasticsearch.engine.elasticsearchengine.proxy.repository;

import com.elasticsearch.engine.elasticsearchengine.execute.resultmodel.SupplierItemEntity;
import com.elasticsearch.engine.elasticsearchengine.mapping.annotation.Collapse;
import com.elasticsearch.engine.elasticsearchengine.mapping.annotation.From;
import com.elasticsearch.engine.elasticsearchengine.mapping.annotation.Term;
import com.elasticsearch.engine.elasticsearchengine.mapping.annotation.Terms;
import com.elasticsearch.engine.elasticsearchengine.model.annotion.EsQueryIndex;
import com.elasticsearch.engine.elasticsearchengine.model.domain.BaseESRepository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * EsAccountMapper
 * response:
 * 1.T *
 * 2.自定义 *
 *
 * @author JohenTeng
 * @date 2021/12/9
 */
//@EsQueryIndex(index = "supplier_item_spare")
//@EsQueryIndex(index = "${es.index.name}")
@EsQueryIndex(value = "${es.index.name:supplier_item_spare}")
public interface SupplierItemParamRepository extends BaseESRepository<SupplierItemEntity, String> {

    /**
     * 查询单个
     *
     * @param itemNo
     * @return
     */
    SupplierItemEntity queryOne(@Term String itemNo, @Collapse Integer status);

    /**
     * List查询
     *
     * @return
     */
    List<SupplierItemEntity> queryList(@Terms List<String> itemNoList);

    /**
     * 时间查询
     *
     * @return
     */
    List<SupplierItemEntity> queryList(@From LocalDateTime createDt);


}
