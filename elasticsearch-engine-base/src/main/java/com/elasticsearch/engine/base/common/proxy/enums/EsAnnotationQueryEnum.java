package com.elasticsearch.engine.base.common.proxy.enums;

import java.util.Arrays;
import java.util.Optional;

/**
 * @author wanghuan
 * @description: es注解查询类型枚举
 * @date 2022-04-15 18:16
 */
public enum EsAnnotationQueryEnum {

    /**
     * 
     */
    ANNOTATION_MODEL_QUERY(1, "model注解"),
    ANNOTATION_PARAM_QUERY(2, "方法参数注解"),
    ;

    private Integer key;
    private String value;

    EsAnnotationQueryEnum(Integer key, String value) {
        this.key = key;
        this.value = value;
    }

    public static EsQueryType of(Integer key) {
        Optional<EsQueryType> exportEnum = Arrays.stream(EsQueryType.values())
                .filter(c -> c.getKey().equals(key)).findFirst();
        return exportEnum.orElse(null);
    }
}
