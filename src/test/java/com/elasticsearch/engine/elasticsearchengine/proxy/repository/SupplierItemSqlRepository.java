package com.elasticsearch.engine.elasticsearchengine.proxy.repository;

import com.elasticsearch.engine.elasticsearchengine.execute.resultmodel.SupplierItemEntity;
import com.elasticsearch.engine.elasticsearchengine.model.annotion.EsQuery;
import com.elasticsearch.engine.elasticsearchengine.model.annotion.EsQueryIndex;
import com.elasticsearch.engine.elasticsearchengine.model.domain.BaseESRepository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author wanghuan
 * @description: ROOD
 * @date 2022-04-25 21:24
 */
@EsQueryIndex(index = "supplier_item_spare")
public interface SupplierItemSqlRepository extends BaseESRepository<SupplierItemEntity, String> {


    @EsQuery("select * from supplier_item_spare where item_no = #{itemNo} and status = #{status}")
    SupplierItemEntity queryOne(String itemNo, Integer status);

    @EsQuery("select * from supplier_item_spare where item_no in (#{status})")
    List<SupplierItemEntity> queryList(List<String> status);

    @EsQuery("select * from supplier_item_spare where createDt > #{createDt}")
    SupplierItemEntity queryByCreateDt(LocalDateTime createDt);

}
