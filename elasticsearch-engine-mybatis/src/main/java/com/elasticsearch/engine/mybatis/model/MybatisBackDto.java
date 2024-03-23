package com.elasticsearch.engine.mybatis.model;

import com.elasticsearch.engine.base.model.domain.BackDto;
import com.elasticsearch.engine.mybatis.annotion.MybatisEsQuery;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Method;
import java.util.Objects;

/**
 * @author wanghuan
 * @description MybatisBackDto
 * @mail 958721894@qq.com
 * @date 2022-06-17 21:11
 */
public class MybatisBackDto extends BackDto {

    public static BackDto hasBack(Method method) {
        MybatisEsQuery esQuery = method.getAnnotation(MybatisEsQuery.class);
        String backColumn = esQuery.backColumn();
        String tableName = esQuery.backTable();
        Class<?> backColumnTyp = esQuery.backColumnType();
        if (StringUtils.isNotEmpty(backColumn) && Objects.nonNull(backColumnTyp) && !backColumnTyp.equals(Objects.class)) {
            return BackDto.builder().tableName(tableName).backColumn(backColumn).backColumnTyp(backColumnTyp).build();
        }
        return null;
    }
}
