package com.elasticsearch.engine.base.model.annotion;

import java.lang.annotation.*;

/**
 * @author wanghuan
 * @description: Ignore
 * 表示忽略某个字段 ,被忽略的字段 无论属性值是否为空, 查询时都不会被解析
 * @date 2022-01-26 11:28
 */
@Inherited
@Documented
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Ignore {
}
