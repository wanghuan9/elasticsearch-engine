package com.elasticsearch.engine.base.mapping.model;

import com.elasticsearch.engine.base.model.constant.CommonConstant;
import com.elasticsearch.engine.base.model.domain.AbstractQueryBean;
import com.elasticsearch.engine.base.model.domain.EsQueryFieldBean;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.index.query.RangeQueryBuilder;

import java.time.LocalDateTime;

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
    public void configQueryBuilder(EsQueryFieldBean queryDes, RangeQueryBuilder queryBuilder) {
        boolean isLocalDateTime = LocalDateTime.class.isAssignableFrom(queryDes.getValue().getClass());
        if (StringUtils.isNotBlank(format)) {
            queryBuilder.format(format);
        } else {
            if (isLocalDateTime) {
                queryBuilder.format(CommonConstant.DEFAULT_DATE_FORMAT);
            }
        }
        if (StringUtils.isNotBlank(relation)) {
            queryBuilder.relation(relation);
        }
        if (StringUtils.isNotBlank(timeZone)) {
            queryBuilder.timeZone(timeZone);
        } else {
            if (isLocalDateTime) {
                queryBuilder.timeZone(CommonConstant.DEFAULT_TIME_ZONE);
            }
        }
    }
}
