package com.elasticsearch.engine.common.proxy.enums;

import java.util.Arrays;
import java.util.Optional;

/**
 * @author wanghuan
 * @description: es sql查询类型枚举
 * @date 2022-04-15 18:16
 */
public enum EsSqlQueryEnum {

    /**
     * 
     */
    ANNOTATION_QUERY(1, "注解查询"),
    XML_QUERY(2, "xml查询"),
    ;

    private Integer key;
    private String value;

    EsSqlQueryEnum(Integer key, String value) {
        this.key = key;
        this.value = value;
    }

    public static EsQueryType of(Integer key) {
        Optional<EsQueryType> exportEnum = Arrays.stream(EsQueryType.values())
                .filter(c -> c.getKey().equals(key)).findFirst();
        return exportEnum.orElse(null);
    }
}
