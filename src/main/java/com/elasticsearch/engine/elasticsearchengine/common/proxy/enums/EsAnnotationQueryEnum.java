package com.elasticsearch.engine.elasticsearchengine.common.proxy.enums;

import java.util.Arrays;
import java.util.Optional;

/**
 * @author wanghuan
 * @description: es注解查询类型枚举
 * @date 2022-04-15 18:16
 */
public enum EsAnnotationQueryEnum {
    REPOSITORY_ENTITY(1, "model注解"),
    CUSTOM_ENTITY(2, "方法参数注解"),
    ;

    private Integer key;
    private String value;

    EsAnnotationQueryEnum(Integer key, String value) {
        this.key = key;
        this.value = value;
    }

    public static EsQueryProxyExecuteEnum of(Integer key) {
        Optional<EsQueryProxyExecuteEnum> exportEnum = Arrays.stream(EsQueryProxyExecuteEnum.values())
                .filter(c -> c.getKey().equals(key)).findFirst();
        return exportEnum.orElse(null);
    }
}
