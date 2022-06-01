package com.elasticsearch.engine.mapping.annotation;


import com.elasticsearch.engine.model.annotion.Base;
import com.elasticsearch.engine.model.annotion.Query;
import com.elasticsearch.engine.model.annotion.Sign;

import java.lang.annotation.*;

/**
 * @author wanghuan
 * @description: 标记注解 Exist
 * exists 查询可以用于查找文档中是否包含指定字段或没有某个字段，或判断某个字段是否为null/或者不为null , 类似于SQL语句中的 is null/is not null
 * EsConnector.MUST 查找文档中是否包含指定字段 is not null (查询field != null的记录)
 * EsConnector.NOT_MUST 查找文档中是否包含指定字段 is null (查询field == null的记录)
 * @date 2022-02-11 17:37
 */
@Sign
@Query
@Inherited
@Documented
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface Exist {

    Base value() default @Base;
}
