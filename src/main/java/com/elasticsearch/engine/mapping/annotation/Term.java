package com.elasticsearch.engine.mapping.annotation;


import com.elasticsearch.engine.model.annotion.Base;
import com.elasticsearch.engine.model.annotion.Query;

import java.lang.annotation.*;

/**
 * @author wanghuan
 * @description: Term
 * @date 2022-01-26 11:28
 */
@Query
@Inherited
@Documented
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface Term {

    Base value() default @Base;

}
