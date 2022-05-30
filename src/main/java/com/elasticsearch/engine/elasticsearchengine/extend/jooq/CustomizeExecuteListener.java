package com.elasticsearch.engine.elasticsearchengine.extend.jooq;

import org.jooq.ExecuteContext;
import org.jooq.ExecuteListener;
import org.jooq.ExecuteListenerProvider;
import org.jooq.ExecuteType;
import org.jooq.impl.DefaultExecuteListener;
import org.jooq.tools.StopWatch;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Optional;

/**
 * @author wanghuan
 * @description: ROOD
 * @date 2022-05-30 08:55
 */

@Component("customizeExecuteListener")
public class CustomizeExecuteListener extends DefaultExecuteListener implements ExecuteListenerProvider {
    private static final Logger LOGGER = LoggerFactory.getLogger(CustomizeExecuteListener.class);

    private StopWatch watch;

    @Override
    public ExecuteListener provide() {
        return this;
    }

    @Override
    public void executeStart(ExecuteContext ctx) {
        
        super.executeStart(ctx);
        watch = new StopWatch();
    }

    @Override
    public void executeEnd(ExecuteContext ctx) {
        try {
            super.executeEnd(ctx);
            //记录执行时间大于1ms(1s:1_000_000_000L)的操作
            if (watch.split() <= 1_000_000L) {
                return;
            }
            ExecuteType type = ctx.type();
            StringBuffer sqlBuffer = new StringBuffer();
            if (type == ExecuteType.BATCH) {
                Arrays.stream(ctx.batchQueries()).forEach(query -> sqlBuffer.append(query.toString()));
            } else {
                sqlBuffer.append(Optional.ofNullable(type).map(ExecuteType::toString).orElse("ExecuteType is null"));
            }
            watch.splitInfo(String.format("The type of database interaction that is being executed with this context : { %s }", sqlBuffer.toString()));
        } catch (Exception e) {
            LOGGER.error(" SlowQueryListener.executeEnd.Exception ", e);
        }
    }
}