package com.elasticsearch.engine.extend.jpa;

import com.elasticsearch.engine.common.utils.ThreadLocalUtil;
import com.elasticsearch.engine.model.constant.CommonConstant;
import com.elasticsearch.engine.model.exception.EsHelperJpaExecuteException;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.resource.jdbc.spi.StatementInspector;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
* @author wanghuan
* @description jpa sql切es查询拦截器
* @mail 958721894@qq.com       
* @date 2022/6/9 11:42 
*/
@Component
public class JpaEsQueryStatementInspector implements StatementInspector {
//    @Override
//    public String inspect(String sql) {
//        Boolean isEsQuery = ThreadLocalUtil.get(CommonConstant.IS_ES_QUERY);
//        String backSql = ThreadLocalUtil.get(CommonConstant.BACK_QUERY_SQL);
//        if (Objects.nonNull(isEsQuery) && isEsQuery) {
//            if (StringUtils.isNotEmpty(backSql)) {
//                return backSql;
//            }
//            throw new EsHelperJpaExecuteException(sql);
//        } else {
//            return sql;
//        }
//    }

    @Override
    public String inspect(String sql) {
        //非select语句直接返回
        if (!sql.startsWith(CommonConstant.SELECT_SQL_PREFIX_LOWER) && !sql.startsWith(CommonConstant.SELECT_SQL_PREFIX_UPPER)) {
            return sql;
        }
        Boolean isEsQuery = ThreadLocalUtil.get(CommonConstant.IS_ES_QUERY);
        String backSql = ThreadLocalUtil.get(CommonConstant.BACK_QUERY_SQL);
        if (Objects.nonNull(isEsQuery) && isEsQuery) {
            ThreadLocalUtil.remove(CommonConstant.IS_ES_QUERY);
            throw new EsHelperJpaExecuteException(sql);
        } else if (StringUtils.isNotEmpty(backSql)) {
            ThreadLocalUtil.remove(CommonConstant.BACK_QUERY_SQL);
            return backSql;
        } else {
            return sql;
        }
    }
}
