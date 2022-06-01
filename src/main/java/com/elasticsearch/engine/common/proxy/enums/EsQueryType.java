package com.elasticsearch.engine.common.proxy.enums;

import lombok.Getter;

import java.util.Arrays;
import java.util.Optional;

/**
 * @author wanghuan
 * @description: ES查询执行器handler
 * @date 2022-04-15 18:16
 */
@Getter
public enum EsQueryType {

    /**
     * 
     */
    ANNOTATION(1, "注解查询"),
    SQL(2, "sql查询"),
    ES_NATIVE(3, "es原生语句查询"),
    ;

    private Integer key;
    private String value;

    EsQueryType(Integer key, String value) {
        this.key = key;
        this.value = value;
    }

    public static EsQueryType of(Integer key) {
        Optional<EsQueryType> exportEnum = Arrays.stream(EsQueryType.values())
                .filter(c -> c.getKey().equals(key)).findFirst();
        return exportEnum.orElse(null);
    }

}
