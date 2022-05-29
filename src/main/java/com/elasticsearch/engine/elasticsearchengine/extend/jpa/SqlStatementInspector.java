package com.elasticsearch.engine.elasticsearchengine.extend.jpa;

import com.elasticsearch.engine.elasticsearchengine.model.exception.EsHelperJpaExecuteException;
import org.hibernate.resource.jdbc.spi.StatementInspector;

/**
 * @author wanghuan
 * @description: ROOD
 * @date 2022-05-24 23:17
 */
public class SqlStatementInspector implements StatementInspector {
    @Override
    public String inspect(String sql) {
//        ThreadLocalUtil.set(CommonConstant.QUERY_SQL, sql);
        throw new EsHelperJpaExecuteException(sql);
    }
}
