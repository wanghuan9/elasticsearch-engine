package com.elasticsearch.engine.mapping.model;

import com.elasticsearch.engine.model.domain.AbstractQueryBean;
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
    public void configQueryBuilder(WildcardQueryBuilder queryBuilder) {
    }
}
