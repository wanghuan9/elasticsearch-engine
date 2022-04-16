package com.elasticsearch.engine.elasticsearchengine.common.proxy.enums;

import lombok.Getter;

import java.util.Arrays;
import java.util.Optional;

/**
 * @author wanghuan
 * @description: ES查询执行器handler
 * @date 2022-04-15 18:16
 */
@Getter
public enum EsQueryProxyExecuteEnum {
    ANNOTATION_QUERY(1, "注解查询"),
    SQL_QUERY(2, "sql查询"),
    ES_NATIVE_QUERY(3, "es原生语句查询"),
    ;

    private Integer key;
    private String value;

    EsQueryProxyExecuteEnum(Integer key, String value) {
        this.key = key;
        this.value = value;
    }

    public static EsQueryProxyExecuteEnum of(Integer key) {
        Optional<EsQueryProxyExecuteEnum> exportEnum = Arrays.stream(EsQueryProxyExecuteEnum.values())
                .filter(c -> c.getKey().equals(key)).findFirst();
        return exportEnum.orElse(null);
    }

}
