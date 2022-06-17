package com.elasticsearch.engine.base.model.domain;

import lombok.Data;

/**
 * @author wanghuan
 * @description: 默认的分组查询结果
 * @date 2022-01-28 15:55
 */
@Data
public class DefaultAggResp implements DefaultResp{

    private String key;

    private Long count;
}
