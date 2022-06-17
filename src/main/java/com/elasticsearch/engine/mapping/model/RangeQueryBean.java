package com.elasticsearch.engine.mapping.model;

import com.elasticsearch.engine.mapping.model.extend.RangeParam;
import com.elasticsearch.engine.model.constant.CommonConstant;
import com.elasticsearch.engine.model.domain.AbstractQueryBean;
import com.elasticsearch.engine.model.domain.EsQueryFieldBean;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.index.query.RangeQueryBuilder;

import java.time.LocalDateTime;
import java.util.Objects;

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
    public void configQueryBuilder(EsQueryFieldBean queryDes, RangeQueryBuilder queryBuilder) {
        RangeParam rangeParam = (RangeParam) queryDes.getValue();
        boolean leftIsLocalDateTime = Objects.nonNull(rangeParam.getLeft()) && LocalDateTime.class.isAssignableFrom(rangeParam.getLeft().getClass());
        boolean rightIsLocalDateTime = Objects.nonNull(rangeParam.getRight()) && LocalDateTime.class.isAssignableFrom(rangeParam.getRight().getClass());
        boolean isLocalDateTime = leftIsLocalDateTime || rightIsLocalDateTime;
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
