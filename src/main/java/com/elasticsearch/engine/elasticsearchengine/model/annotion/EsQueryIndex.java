package com.elasticsearch.engine.elasticsearchengine.model.annotion;


import com.elasticsearch.engine.elasticsearchengine.model.emenu.QueryModel;

import java.lang.annotation.*;

/**
 * Define the esQueryHolder's index-name, queryModel ... information
 *
 * @author wanghuan
 * @date 2022-01-26 11:28
 */
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface EsQueryIndex {

    /**
     * 查询索引
     *
     * @return
     */
    String index();

    /**
     * 查询方式 默认bool
     * query model {@link QueryModel} (required)
     *
     * @see QueryModel
     * return
     */
    QueryModel model() default QueryModel.BOOL;

    /**
     * exclude unuseful fields from ES
     * return
     */
    String[] include() default {};

    /**
     * exclude unuseful fields from ES
     * return
     */
    String[] exclude() default {};


}
