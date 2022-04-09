package com.elasticsearch.engine.elasticsearchengine.mapping.annotation;


import com.elasticsearch.engine.elasticsearchengine.model.annotion.Base;
import com.elasticsearch.engine.elasticsearchengine.model.annotion.Query;
import com.elasticsearch.engine.elasticsearchengine.model.annotion.Sign;

import java.lang.annotation.*;

/**
 * @author wanghuan
 * @description: 标记注解 Collapse
 * @date 2022-01-26 11:28
 */
@Sign
@Query
@Inherited
@Documented
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Collapse {

    Base value() default @Base;

    /**
     * ES分组查询不设置size,默认只返回10条数据
     */
    int size() default 2000;
}
