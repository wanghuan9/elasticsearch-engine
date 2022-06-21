package com.elasticsearch.engine.jpa.model;

import com.elasticsearch.engine.base.model.domain.BackDto;
import com.elasticsearch.engine.jpa.annotion.JpaEsQuery;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Method;
import java.util.Objects;

/**
 * @author wanghuan
 * @description MybatisBackDto
 * @mail 958721894@qq.com
 * @date 2022-06-17 21:11
 */
public class JpaBackDto extends BackDto {

    public static BackDto hasJpaBack(Method method) {
        JpaEsQuery esQuery = method.getAnnotation(JpaEsQuery.class);
        String backColumn = esQuery.backColumn();
        Class<?> backColumnTyp = esQuery.backColumnType();
        if (StringUtils.isNotEmpty(backColumn) && Objects.nonNull(backColumnTyp) && !backColumnTyp.equals(Objects.class)) {
            return BackDto.builder().backColumn(backColumn).backColumnTyp(backColumnTyp).build();
        }
        return null;
    }
}
