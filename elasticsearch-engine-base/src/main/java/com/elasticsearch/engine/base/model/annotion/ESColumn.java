package com.elasticsearch.engine.base.model.annotion;

import java.lang.annotation.*;

/**
 * @author wanghuan
 * @description ESColumn
 * @date 2022/7/15 17:03
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.ANNOTATION_TYPE})
@Documented
public @interface ESColumn {

    String table() default "";

    String sqlColumn() default "";

    String esColumn() default "";
}
