package com.elasticsearch.engine.elasticsearchengine.mapping.model;

import com.elasticsearch.engine.elasticsearchengine.model.domain.AbstractQueryBean;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.index.query.RangeQueryBuilder;

/**
 * @author wanghuan
 * @description: FromQueryBean
 * @date 2022-02-07 10:12
 */
@Data
public class ToQueryBean extends AbstractQueryBean<RangeQueryBuilder> {

    private String format;
    private String relation;
    private String timeZone;

    private int group;

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
