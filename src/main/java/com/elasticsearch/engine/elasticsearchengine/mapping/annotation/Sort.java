package com.elasticsearch.engine.elasticsearchengine.mapping.annotation;

import com.elasticsearch.engine.elasticsearchengine.model.annotion.Base;
import com.elasticsearch.engine.elasticsearchengine.model.annotion.Query;
import com.elasticsearch.engine.elasticsearchengine.model.annotion.Sign;
import org.elasticsearch.search.sort.SortOrder;

import java.lang.annotation.*;

/**
 * @author wanghuan
 * @description: 标记注解 Sort
 * @date 2022-01-26 11:28
 */
@Sign
@Query
@Inherited
@Documented
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface Sort {

    Base value() default @Base;

    SortOrder type() default SortOrder.ASC;
}
