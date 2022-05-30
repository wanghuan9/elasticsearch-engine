package com.elasticsearch.engine.elasticsearchengine.extend.jooq;

import com.elasticsearch.engine.elasticsearchengine.common.utils.ThreadLocalUtil;
import com.elasticsearch.engine.elasticsearchengine.model.constant.CommonConstant;
import com.elasticsearch.engine.elasticsearchengine.model.exception.EsHelperJpaExecuteException;
import org.jooq.ExecuteContext;
import org.jooq.ExecuteListener;
import org.jooq.ExecuteListenerProvider;
import org.jooq.impl.DefaultExecuteListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * @author wanghuan
 * @description: ROOD
 * @date 2022-05-30 08:55
 */

@Component("customizeExecuteListener")
public class CustomizeExecuteListener extends DefaultExecuteListener implements ExecuteListenerProvider {
    private static final Logger LOGGER = LoggerFactory.getLogger(CustomizeExecuteListener.class);

    @Override
    public ExecuteListener provide() {
        return this;
    }

    @Override
    public void executeStart(ExecuteContext ctx) {
        String sql = ctx.sql();
        Boolean isEsQuery = ThreadLocalUtil.get(CommonConstant.IS_ES_QUERY);
        if (Objects.nonNull(isEsQuery) && isEsQuery) {
            throw new EsHelperJpaExecuteException(sql);
        } 
    }

    @Override
    public void executeEnd(ExecuteContext ctx) {
    }
}