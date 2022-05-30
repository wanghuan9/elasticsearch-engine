package com.elasticsearch.engine.elasticsearchengine.model.annotion;

import java.lang.annotation.*;


/**
 * @author wanghuan
 * @description: EsQuery
 * @date 2022-01-26 11:28
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.ANNOTATION_TYPE})
@Documented
public @interface JooqEsQuery {

}
