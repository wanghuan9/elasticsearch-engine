package com.elasticsearch.engine.base.mapping.model;

import com.elasticsearch.engine.base.model.domain.AbstractQueryBean;
import com.elasticsearch.engine.base.model.domain.EsQueryFieldBean;
import lombok.Data;
import org.elasticsearch.index.query.WildcardQueryBuilder;

/**
 * @author wanghuan
 * @description: WildCardQueryBean
 * @date 2022-01-26 11:28
 */
@Data
public class WildCardQueryBean extends AbstractQueryBean<WildcardQueryBuilder> {

    /**
     * 匹配类型
     *
     * @see com.example.springbootelasticsearch.common.engine.mapping.annotation.WildCard
     */
    private String tag;

    @Override
    public void configQueryBuilder(EsQueryFieldBean queryDes, WildcardQueryBuilder queryBuilder) {
    }
}
