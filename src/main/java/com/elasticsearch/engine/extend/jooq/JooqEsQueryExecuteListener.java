package com.elasticsearch.engine.extend.jooq;

import com.elasticsearch.engine.common.utils.ThreadLocalUtil;
import com.elasticsearch.engine.model.constant.CommonConstant;
import com.elasticsearch.engine.model.exception.EsEngineJpaExecuteException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jooq.ExecuteContext;
import org.jooq.ExecuteListener;
import org.jooq.ExecuteListenerProvider;
import org.jooq.impl.DefaultExecuteListener;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
* @author wanghuan
* @description jooq sql切es查询拦截器
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

    @Override
    public void renderEnd(ExecuteContext ctx) {
        String backSql = ThreadLocalUtil.get(CommonConstant.BACK_QUERY_SQL);
        if (StringUtils.isNotEmpty(backSql)) {
            //test
//            ctx.sql("SELECT `user`.`person`.`id`, `user`.`person`.`person_no`, `user`.`person`.`person_name`, `user`.`person`.`phone`, `user`.`person`.`salary`, `user`.`person`.`company`, `user`.`person`.`status`, `user`.`person`.`sex`, `user`.`person`.`address`, `user`.`person`.`create_time`, `user`.`person`.`create_user` FROM `user`.`person` WHERE `user`.`person`.`status` = ? AND person_no IN ('US2022060100001', 'US2022060100023')");
            ctx.sql(backSql);
            ThreadLocalUtil.remove(CommonConstant.IS_ES_QUERY);
        }
    }

    @Override
    public void executeEnd(ExecuteContext ctx) {
//        log.info("jooq回表执行sql: " + ctx.sql());
    }

    public void start(ExecuteContext ctx) {
        String sql = ctx.query().getSQL();
        //非select语句直接返回
        if (!sql.startsWith(CommonConstant.SELECT_SQL_PREFIX_LOWER) && !sql.startsWith(CommonConstant.SELECT_SQL_PREFIX_UPPER)) {
            return;
        }
        Boolean isEsQuery = ThreadLocalUtil.get(CommonConstant.IS_ES_QUERY);
        if (Objects.nonNull(isEsQuery) && isEsQuery) {
            ThreadLocalUtil.remove(CommonConstant.IS_ES_QUERY);
            throw new EsEngineJpaExecuteException(sql);
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

}