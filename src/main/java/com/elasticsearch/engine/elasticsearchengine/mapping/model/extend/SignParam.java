package com.elasticsearch.engine.elasticsearchengine.mapping.model.extend;


import com.elasticsearch.engine.elasticsearchengine.model.domain.EsComplexParam;

/**
 * @author wanghuan
 * @description: SignParam 标记字段可设置的类型
 * @date 2022-03-29 14:29
 */
public class SignParam implements EsComplexParam {
    public static SignParam builder() {
        return new SignParam();
    }
}
