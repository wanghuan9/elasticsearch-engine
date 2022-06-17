package com.elasticsearch.engine.base.model.domain;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author wanghuan
 * @description: BaseResp
 * @date 2022-01-26 11:28
 */
@Data
public class BaseResp<T> implements Serializable {

    //查询结果的总记录数
    private Long totalHit;

    //默认返回List<T> 结果
    private List<T> records;

    //自定义的result,
    //T可以定义为任意Object, List<Object> , Map<Object,Object> 等
    private T result;
}
