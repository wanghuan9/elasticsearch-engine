package com.elasticsearch.engine.base.mapping.annotation;


import com.elasticsearch.engine.base.model.annotion.Base;
import com.elasticsearch.engine.base.model.annotion.Query;

import java.lang.annotation.*;

/**
 * @author wanghuan
 * @description: Terms
 * @date 2022-01-26 11:28
 */
@Query
@Inherited
@Documented
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface Terms {

    Base value() default @Base;

}
