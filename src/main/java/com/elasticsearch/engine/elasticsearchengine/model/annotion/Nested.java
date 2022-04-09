package com.elasticsearch.engine.elasticsearchengine.model.annotion;

import java.lang.annotation.*;

/**
 * @author wanghuan
 * @description: 解析嵌套实体类需要添加该注解
 * @date 2022-02-10 11:09
 */
@Inherited
@Documented
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Nested {
}
