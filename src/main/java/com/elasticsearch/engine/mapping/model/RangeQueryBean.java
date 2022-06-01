package com.elasticsearch.engine.mapping.model;

import com.elasticsearch.engine.model.domain.AbstractQueryBean;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.index.query.RangeQueryBuilder;

/**
 * @author wanghuan
 * @description: RangeQueryBean
 * @date 2022-01-26 11:28
 */
@Data
public class RangeQueryBean extends AbstractQueryBean<RangeQueryBuilder> {

    /**
     * {
     *
     * @link com.example.springbootelasticsearch.common.engine.mapping.annotation.Range#LE_GE
     * }
     */
    private String tag;
    private String format;
    private String relation;
    private String timeZone;

    @Override
    public void configQueryBuilder(RangeQueryBuilder queryBuilder) {
        if (StringUtils.isNotBlank(format)) {
            queryBuilder.format(format);
        }
        if (StringUtils.isNotBlank(relation)) {
            queryBuilder.relation(relation);
        }
        if (StringUtils.isNotBlank(timeZone)) {
            queryBuilder.timeZone(timeZone);
        }
    }

}
