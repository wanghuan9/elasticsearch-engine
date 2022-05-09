package com.elasticsearch.engine.elasticsearchengine.extend.mybatis.mapper;

import com.elasticsearch.engine.elasticsearchengine.model.annotion.EsQuery;
import com.elasticsearch.engine.elasticsearchengine.model.exception.ItemsEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author wanghuan
 * @description: ROOD
 * @date 2022-05-07 23:37
 */
@Mapper
public interface ItemsMapper {

    @EsQuery
    ItemsEntity getByItemNo(String itemNo);
}
