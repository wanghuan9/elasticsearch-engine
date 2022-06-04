package com.elasticsearch.engine.extend.jpa;

import com.elasticsearch.engine.common.utils.ThreadLocalUtil;
import com.elasticsearch.engine.model.constant.CommonConstant;
import com.elasticsearch.engine.model.exception.EsHelperJpaExecuteException;
import org.hibernate.resource.jdbc.spi.StatementInspector;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * @author wanghuan
 * @description: ROOD
 * @date 2022-05-24 23:17
 */
@Component
public class SqlStatementInspector implements StatementInspector {
    @Override
    public String inspect(String sql) {
        Boolean isEsQuery = ThreadLocalUtil.get(CommonConstant.IS_ES_QUERY);
        if (Objects.nonNull(isEsQuery) && isEsQuery) {
            throw new EsHelperJpaExecuteException(sql);
        } else {
            return sql;
        }
    }
}
