package com.elasticsearch.engine.elasticsearchengine.extend.jpa.repository;

import com.elasticsearch.engine.elasticsearchengine.extend.jpa.test.ItemEntity;
import com.elasticsearch.engine.elasticsearchengine.model.annotion.EsQuery;
import com.elasticsearch.engine.elasticsearchengine.model.annotion.EsQueryIndex;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @author wanghuan
 * @description: ROOD
 * @date 2022-05-25 21:58
 */
@EsQueryIndex("supplier_item_spare")
public interface ItemRepository extends JpaRepository<ItemEntity, String> {

    @EsQuery
    ItemEntity getByItemNo(String itemNo);
    
}
