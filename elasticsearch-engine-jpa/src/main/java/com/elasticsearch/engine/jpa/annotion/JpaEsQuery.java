package com.elasticsearch.engine.jpa.annotion;

import java.lang.annotation.*;


/**
 * @author wanghuan
 * @description: EsQuery
 * @date 2022-01-26 11:28
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.ANNOTATION_TYPE})
@Documented
public @interface JpaEsQuery {

    /**
     * 回表字段所属的表名或别名
     *
     * @return
     */
    String tableName() default "";

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
