package com.elasticsearch.engine.elasticsearchengine.model.domain;

import com.elasticsearch.engine.elasticsearchengine.mapping.annotation.PageAndOrder;
import com.elasticsearch.engine.elasticsearchengine.mapping.annotation.Term;
import com.elasticsearch.engine.elasticsearchengine.mapping.annotation.Terms;
import lombok.Data;

/**
 * @author wanghuan
 * @description: DefaultQueryModel
 * @date 2022-01t20 2:20
 */
@Data
public class DefaultQueryModel {

    @Term
    public String term;

    @Terms
    public String terms;

    @PageAndOrder
    public String pageAndOrder;

    public static DefaultQueryModel build() {
        return new DefaultQueryModel();
    }
}
