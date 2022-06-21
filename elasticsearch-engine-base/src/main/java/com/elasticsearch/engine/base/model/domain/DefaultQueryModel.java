package com.elasticsearch.engine.base.model.domain;

import com.elasticsearch.engine.base.mapping.annotation.PageAndOrder;
import com.elasticsearch.engine.base.mapping.annotation.Term;
import com.elasticsearch.engine.base.mapping.annotation.Terms;
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
