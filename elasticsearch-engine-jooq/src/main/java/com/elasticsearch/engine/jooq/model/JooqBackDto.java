package com.elasticsearch.engine.jooq.model;

import com.elasticsearch.engine.base.model.domain.BackDto;
import com.elasticsearch.engine.jooq.annotion.JooqEsQuery;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Method;
import java.util.Objects;

/**
 * @author wanghuan
 * @description MybatisBackDto
 * @mail 958721894@qq.com
 * @date 2022-06-17 21:11
 */
public class JooqBackDto extends BackDto {

    public static BackDto hasJooqBack(Method method) {
        JooqEsQuery esQuery = method.getAnnotation(JooqEsQuery.class);
        String backColumn = esQuery.backColumn();
        String tableName = esQuery.tableName();
        Class<?> backColumnTyp = esQuery.backColumnType();
        if (StringUtils.isNotEmpty(backColumn) && Objects.nonNull(backColumnTyp) && !backColumnTyp.equals(Objects.class)) {
            return BackDto.builder().tableName(tableName).backColumn(backColumn).backColumnTyp(backColumnTyp).build();
        }
        return null;
    }
}
