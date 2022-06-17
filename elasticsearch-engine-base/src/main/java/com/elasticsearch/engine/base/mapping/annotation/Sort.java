package com.elasticsearch.engine.base.mapping.annotation;

import com.elasticsearch.engine.base.model.annotion.Base;
import com.elasticsearch.engine.base.model.annotion.Query;
import com.elasticsearch.engine.base.model.annotion.Sign;
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
