package com.elasticsearch.engine.extend.jooq;

import com.elasticsearch.engine.common.utils.ThreadLocalUtil;
import com.elasticsearch.engine.model.constant.CommonConstant;
import com.elasticsearch.engine.model.exception.EsHelperJpaExecuteException;
import org.apache.commons.lang3.StringUtils;
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
        String backSql = ThreadLocalUtil.get(CommonConstant.BACK_QUERY_SQL);
        if (Objects.nonNull(isEsQuery) && isEsQuery) {
            if (StringUtils.isNotEmpty(backSql)) {
                ctx.sql(backSql);
                return;
            }
            throw new EsHelperJpaExecuteException(sql);
        } 
    }

    @Override
    public void executeEnd(ExecuteContext ctx) {
    }
}