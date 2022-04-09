package com.elasticsearch.engine.elasticsearchengine.model.domain;

import lombok.Data;

/**
 * @author wanghuan
 * @description: 默认的分组查询结果
 * @date 2022-01-28 15:55
 */
@Data
public class DefaultAggResp {

    private String key;

    private Long count;
}
