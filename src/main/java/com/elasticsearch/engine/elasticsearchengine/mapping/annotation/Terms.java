package com.elasticsearch.engine.elasticsearchengine.mapping.annotation;


import com.elasticsearch.engine.elasticsearchengine.model.annotion.Base;
import com.elasticsearch.engine.elasticsearchengine.model.annotion.Query;

import java.lang.annotation.*;

/**
 * @author wanghuan
 * @description: Terms
 * @date 2022-01-26 11:28
 */
@Query
@Inherited
@Documented
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Terms {

    Base value() default @Base;

}
