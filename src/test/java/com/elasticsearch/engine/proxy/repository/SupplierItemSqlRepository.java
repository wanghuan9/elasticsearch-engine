package com.elasticsearch.engine.proxy.repository;

import com.elasticsearch.engine.execute.resultmodel.SupplierItemEntity;
import com.elasticsearch.engine.model.annotion.EsQuery;
import com.elasticsearch.engine.model.annotion.EsQueryIndex;
import com.elasticsearch.engine.model.domain.BaseESRepository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author wanghuan
 * @description: ROOD
 * @date 2022-04-25 21:24
 */
@EsQueryIndex(value = "supplier_item_spare")
public interface SupplierItemSqlRepository extends BaseESRepository<SupplierItemEntity, String> {


    @EsQuery("SELECT * FROM supplier_item_spare WHERE item_no = #{itemNo} AND status = #{status}")
    SupplierItemEntity queryOne(String itemNo, Integer status2);

    @EsQuery("SELECT * FROM supplier_item_spare WHERE item_no IN (#{status})")
    List<SupplierItemEntity> queryList(List<String> status);

    @EsQuery("SELECT * FROM supplier_item_spare WHERE create_dt > #{createDt}")
    SupplierItemEntity queryByCreateDt(LocalDateTime createDt);

    @EsQuery("SELECT COUNT(1) FROM supplier_item_spare")
    Long count();

}
