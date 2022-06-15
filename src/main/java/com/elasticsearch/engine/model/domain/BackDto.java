package com.elasticsearch.engine.model.domain;

import com.elasticsearch.engine.model.annotion.JooqEsQuery;
import com.elasticsearch.engine.model.annotion.JpaEsQuery;
import com.elasticsearch.engine.model.annotion.MybatisEsQuery;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Method;
import java.util.Objects;

/**
 * @author wanghuan
 * @description BackDto
 * @mail 958721894@qq.com
 * @date 2022-06-05 15:44
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BackDto {

    private String backColumn;
    private Class<?> backColumnTyp;

    public static BackDto hasBack(Method method) {
        MybatisEsQuery esQuery = method.getAnnotation(MybatisEsQuery.class);
        String backColumn = esQuery.backColumn();
        Class<?> backColumnTyp = esQuery.backColumnType();
        if (StringUtils.isNotEmpty(backColumn) && Objects.nonNull(backColumnTyp) && !backColumnTyp.equals(Objects.class)) {
            return BackDto.builder().backColumn(backColumn).backColumnTyp(backColumnTyp).build();
        }
        return null;
    }

    public static BackDto hasJpaBack(Method method) {
        JpaEsQuery esQuery = method.getAnnotation(JpaEsQuery.class);
        String backColumn = esQuery.backColumn();
        Class<?> backColumnTyp = esQuery.backColumnType();
        if (StringUtils.isNotEmpty(backColumn) && Objects.nonNull(backColumnTyp) && !backColumnTyp.equals(Objects.class)) {
            return BackDto.builder().backColumn(backColumn).backColumnTyp(backColumnTyp).build();
        }
        return null;
    }

    public static BackDto hasJooQBack(Method method) {
        JooqEsQuery esQuery = method.getAnnotation(JooqEsQuery.class);
        String backColumn = esQuery.backColumn();
        Class<?> backColumnTyp = esQuery.backColumnType();
        if (StringUtils.isNotEmpty(backColumn) && Objects.nonNull(backColumnTyp) && !backColumnTyp.equals(Objects.class)) {
            return BackDto.builder().backColumn(backColumn).backColumnTyp(backColumnTyp).build();
        }
        return null;
    }
}
