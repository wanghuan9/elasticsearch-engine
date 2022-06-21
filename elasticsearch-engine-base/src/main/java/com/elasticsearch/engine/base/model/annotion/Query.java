package com.elasticsearch.engine.base.model.annotion;

import java.lang.annotation.*;

/**
 * @author wanghuan
 * @description: Query
 * @date 2022-01-26 11:28
 */
@Inherited
@Documented
@Target({ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Query {
}
