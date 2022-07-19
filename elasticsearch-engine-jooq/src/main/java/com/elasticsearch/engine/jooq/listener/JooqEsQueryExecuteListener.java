package com.elasticsearch.engine.jooq.listener;

import com.elasticsearch.engine.base.common.utils.LocalStringUtils;
import com.elasticsearch.engine.base.common.utils.ThreadLocalUtil;
import com.elasticsearch.engine.base.model.constant.CommonConstant;
import com.elasticsearch.engine.base.model.exception.EsEngineJpaExecuteException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jooq.ExecuteContext;
import org.jooq.ExecuteListener;
import org.jooq.ExecuteListenerProvider;
import org.jooq.impl.DefaultExecuteListener;
import org.jooq.tools.LoggerListener;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * @author wanghuan
 * @description jooq sql切es查询拦截器
 * 参考:{@link LoggerListener}
 * jooq执行生命周期: {@link DefaultExecuteListener}
 * @mail 958721894@qq.com
 * @date 2022/6/9 11:44
 */
@Order(100)
@Slf4j
@Component("customizeExecuteListener")
public class JooqEsQueryExecuteListener extends DefaultExecuteListener implements ExecuteListenerProvider {

    private static final long serialVersionUID = 912957571617470108L;

    @Override
    public ExecuteListener provide() {
        return this;
    }

    /**
     * 解析完sql
     *
     * @param ctx
     */
    @Override
    public void renderEnd(ExecuteContext ctx) {
        //非es查询
        if (Objects.isNull(ThreadLocalUtil.get(CommonConstant.IS_ES_QUERY))) {
            return;
        }
        if (StringUtils.isEmpty(ctx.sql())) {
            return;
        }
        //非select语句直接返回
        if (!Objects.requireNonNull(ctx.sql()).startsWith(CommonConstant.SELECT_SQL_PREFIX_LOWER) && !Objects.requireNonNull(ctx.sql()).startsWith(CommonConstant.SELECT_SQL_PREFIX_UPPER)) {
            return;
        }
        String backSql = ThreadLocalUtil.get(CommonConstant.BACK_QUERY_SQL);
        if (StringUtils.isNotEmpty(backSql)) {
            //test
//            ctx.sql("SELECT `user`.`person`.`id`, `user`.`person`.`person_no`, `user`.`person`.`person_name`, `user`.`person`.`phone`, `user`.`person`.`salary`, `user`.`person`.`company`, `user`.`person`.`status`, `user`.`person`.`sex`, `user`.`person`.`address`, `user`.`person`.`create_time`, `user`.`person`.`create_user` FROM `user`.`person` WHERE `user`.`person`.`status` = ? AND person_no IN ('US2022060100001', 'US2022060100023')");
            ctx.sql(backSql);
        } else {
            ThreadLocalUtil.set(CommonConstant.JPA_NATIVE_SQL, sqlTransform(Objects.requireNonNull(ctx.sql())));
        }
    }

    /**
     * 执行结束
     *
     * @param ctx
     */
    @Override
    public void executeEnd(ExecuteContext ctx) {
//        log.info("jooq回表执行sql: " + ctx.sql());
    }

    @Override
    public void start(ExecuteContext ctx) {
//        String sql = ctx.query().getSQL();
//        //非select语句直接返回
//        if (!sql.startsWith(CommonConstant.SELECT_SQL_PREFIX_LOWER) && !sql.startsWith(CommonConstant.SELECT_SQL_PREFIX_UPPER)) {
//            return;
//        }
//        Boolean isEsQuery = ThreadLocalUtil.get(CommonConstant.IS_ES_QUERY);
//        if (Objects.nonNull(isEsQuery) && isEsQuery) {
//            ThreadLocalUtil.remove(CommonConstant.IS_ES_QUERY);
//            throw new EsEngineJpaExecuteException(sql);
//        }
    }

    /**
     * 绑定完sql参数
     *
     * @param ctx
     */
    @Override
    public void bindEnd(ExecuteContext ctx) {
        Boolean isEsQuery = ThreadLocalUtil.get(CommonConstant.IS_ES_QUERY);
        if (Objects.isNull(isEsQuery)) {
            return;
        }
        String sql = Objects.requireNonNull(ctx.query()).toString();
        //非select语句直接返回
        if (!sql.trim().startsWith(CommonConstant.SELECT_SQL_PREFIX_LOWER) && !sql.trim().startsWith(CommonConstant.SELECT_SQL_PREFIX_UPPER)) {
            return;
        }
        String backSql = ThreadLocalUtil.get(CommonConstant.BACK_QUERY_SQL);
       
        if (StringUtils.isEmpty(backSql)) {
            throw new EsEngineJpaExecuteException(sqlTransform(sql));
        }
    }


//    @Override
//    public void executeStart(ExecuteContext ctx) {
//        String sql = ctx.sql();
//        Boolean isEsQuery = ThreadLocalUtil.get(CommonConstant.IS_ES_QUERY);
//        String backSql = ThreadLocalUtil.get(CommonConstant.BACK_QUERY_SQL);
//        if (Objects.nonNull(isEsQuery) && isEsQuery) {
//            if (StringUtils.isNotEmpty(backSql)) {
//                Query query = ctx.query();
//                if(query instanceof SelectQuery){
//                    SelectQuery newQuery = (SelectQuery) query;
//                    Condition condition = DSL.trueCondition();
//                    newQuery.addConditions(condition.and("person_no IN ('US2022060100001', 'US2022060100023')"));
//                }
//                ctx.sql("SELECT `user`.`person`.`id`, `user`.`person`.`person_no`, `user`.`person`.`person_name`, `user`.`person`.`phone`, `user`.`person`.`salary`, `user`.`person`.`company`, `user`.`person`.`status`, `user`.`person`.`sex`, `user`.`person`.`address`, `user`.`person`.`create_time`, `user`.`person`.`create_user` FROM `user`.`person` WHERE `user`.`person`.`status` = ? AND person_no IN ( 'US2022060100028', 'US2022060100001', 'US2022060100023')");
//                return;
//            }
//            throw new EsHelperJpaExecuteException(sql);
//        } 
//    }

    private String sqlTransform(String sql) {
        //jooq 需要替换"`"
        sql = LocalStringUtils.replaceSlightPauseMark(sql);
        //处理jooq生成的between
        if (sql.contains(CommonConstant.JOOQ_SQL_BETWEEN)) {
            return sql.replaceAll(CommonConstant.JOOQ_SQL_BETWEEN_PREFIX, "").replaceAll(CommonConstant.JOOQ_SQL_BETWEEN_SUFFIX, "");
        }
        return sql;
    }

}