package com.elasticsearch.engine.base.model.annotion;

import java.lang.annotation.*;


/**
 * @author wanghuan
 * @description: EsQuery
 * @date 2022-01-26 11:28
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.ANNOTATION_TYPE})
@Documented
public @interface JooqEsQuery {

    /**
     * 回表字段
     *
     * @return
     */
    String backColumn() default "";

    /**
     * 回表字段类型
     *
     * @return
     */
    Class<?> backColumnType() default Object.class;

}
