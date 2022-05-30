package com.elasticsearch.engine.elasticsearchengine.extend.jpa;

import com.elasticsearch.engine.elasticsearchengine.common.utils.ThreadLocalUtil;
import com.elasticsearch.engine.elasticsearchengine.model.constant.CommonConstant;
import com.elasticsearch.engine.elasticsearchengine.model.exception.EsHelperJpaExecuteException;
import org.hibernate.resource.jdbc.spi.StatementInspector;

import java.util.Objects;

/**
 * @author wanghuan
 * @description: ROOD
 * @date 2022-05-24 23:17
 */
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
