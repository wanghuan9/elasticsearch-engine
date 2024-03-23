package com.elasticsearch.engine.mybatis.annotion;

import java.lang.annotation.*;

/**
 * @author wanghuan
 * @description MybatisEsQuery
 * @mail 958721894@qq.com
 * @date 2022-06-15 10:12
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.ANNOTATION_TYPE})
@Documented
public @interface MybatisEsQuery {

    
    /**
     * 回表字段所属的表名或别名
     *
     * @return
     */
    String backTable() default "";
    
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
