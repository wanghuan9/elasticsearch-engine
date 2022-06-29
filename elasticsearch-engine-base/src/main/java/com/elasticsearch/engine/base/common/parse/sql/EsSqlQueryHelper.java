package com.elasticsearch.engine.base.common.parse.sql;

import com.elasticsearch.engine.base.common.proxy.handler.exannotation.AnnotationQueryCommon;
import com.elasticsearch.engine.base.common.queryhandler.sql.EsSqlExecuteHandler;
import com.elasticsearch.engine.base.common.utils.ThreadLocalUtil;
import com.elasticsearch.engine.base.config.EsEngineConfig;
import com.elasticsearch.engine.base.model.constant.CommonConstant;
import com.elasticsearch.engine.base.model.domain.BackDto;
import com.elasticsearch.engine.base.model.emenu.SqlParamParse;
import com.elasticsearch.engine.base.model.exception.EsEngineJpaExecuteException;
import lombok.extern.slf4j.Slf4j;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.statement.select.Select;
import org.apache.commons.collections4.CollectionUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Objects;

/**
 * @author wanghuan
 * @description es sql查询核心逻辑
 * @mail 958721894@qq.com
 * @date 2022-06-07 10:17
 */
@Slf4j
@Component
public class EsSqlQueryHelper {

    private static final String ENABLE_LOG_OUT_PROPERTIES = "es.engine.config.sql-trace-log";

    @Resource
    private EsSqlExecuteHandler esSqlExecuteHandler;

    /**
     * es aop 查询逻辑
     *
     * @param pjp
     * @param backDto
     * @return
     * @throws Throwable
     */
    public Object esSqlQueryAopCommon(ProceedingJoinPoint pjp, BackDto backDto) throws Throwable {
        MethodSignature signature = (MethodSignature) pjp.getSignature();
        Method method = signature.getMethod();
        Object[] args = pjp.getArgs();
        Object result = null;
        try {
            ThreadLocalUtil.set(CommonConstant.IS_ES_QUERY, Boolean.TRUE);
            result = pjp.proceed(args);
        } catch (EsEngineJpaExecuteException e) {
            if (Objects.nonNull(backDto)) {
                //回表sql执行, sql重新时使用 原生未绑定参数的sql
                List<?> esResult = esQueryBack(method, e.getMessage(), args, backDto);
                if(CollectionUtils.isEmpty(esResult)){
                    return result;
                }
                result = pjp.proceed(args);
            } else {
                //原生es执行 直接使用绑定参数后的sql
                result = esQuery(method, e.getMessage(), args, backDto);
            }
        } finally {
            ThreadLocalUtil.remove(CommonConstant.IS_ES_QUERY);
            ThreadLocalUtil.remove(CommonConstant.BACK_QUERY_SQL);
        }
        return result;
    }

    /**
     * es查询
     *
     * @param method
     * @param sql
     * @param args
     * @return
     * @throws JSQLParserException
     */
    public Object esQuery(Method method, String sql, Object[] args, BackDto backDto) throws JSQLParserException {
        String paramSql = fillParamSql(method, sql, args, backDto);
        //执行ES查询
        return doQueryEs(paramSql, method);
    }

    /**
     * es回表查询
     *
     * @param method
     * @param sql
     * @param args
     * @param backDto
     * @throws Exception
     */
    public List<?> esQueryBack(Method method, String sql, Object[] args, BackDto backDto) throws Exception {
        String paramSql = fillParamSql(method, sql, args, backDto);
        //执行ES查询
        List<?> esResult = esSqlExecuteHandler.queryBySql(paramSql, backDto.getBackColumnTyp(), Boolean.TRUE);
        if (CollectionUtils.isEmpty(esResult)) {
            return null;
        }
        //将原sql改写成回表sql
        String backSql = SqlParserHelper.rewriteBackSql(sql, backDto, esResult);
        if (EsEngineConfig.getSqlTraceLog()) {
            log.info("回表sql :  {}", backSql);
        }
        //将回表sql添加到threadLocal
        ThreadLocalUtil.set(CommonConstant.BACK_QUERY_SQL, backSql);
        return esResult;
    }

    /**
     * 改写sql填充参数
     *
     * @param method
     * @param sql
     * @param args
     * @param backDto
     * @return
     * @throws JSQLParserException
     */
    private String fillParamSql(Method method, String sql, Object[] args, BackDto backDto) throws JSQLParserException {
        //jpa判断是否清除as别名
        Boolean isCleanAs = Boolean.TRUE;
        if (EsEngineConfig.getSqlTraceLog()) {
            log.info("原始sql: {}", sql);
        }
        //jpa原生查询 则不清楚 as别名
//        Query query = method.getAnnotation(Query.class);
//        if (Objects.nonNull(query) && query.nativeQuery()) {
//            isCleanAs = Boolean.FALSE;
//        }
        //改写sql
        Select select = SqlParserHelper.rewriteSql(method, sql, isCleanAs, backDto);
        if (EsEngineConfig.getSqlTraceLog()) {
            log.info("改写后sql: {}", select);
        }
        //参数替换
        // 解析sql参数
        //jooq 需要替换"`"
        String selectSql = select.toString().replaceAll("`", "");
        String paramSql = SqlParamParseHelper.getMethodArgsSqlJpa(selectSql, method, args, SqlParamParse.JAP_SQL_PARAM);
        if (EsEngineConfig.getSqlTraceLog()) {
            log.info("替换参数后sql: {}", paramSql);
        }
        return paramSql;
    }

    /**
     * 执行es查询
     *
     * @param sql
     * @param method
     * @return
     */
    private Object doQueryEs(String sql, Method method) {
        //方法返回值
        Class<?> returnType = method.getReturnType();
        //方法返回值的泛型
        Class<?> returnGenericType = AnnotationQueryCommon.getReturnGenericType(method);

        List<?> list;
        if (List.class.isAssignableFrom(returnType) && Objects.nonNull(returnGenericType)) {
            list = esSqlExecuteHandler.queryBySql(sql, returnGenericType, Boolean.TRUE);
        } else {
            list = esSqlExecuteHandler.queryBySql(sql, returnType, Boolean.TRUE);
        }
        if (List.class.isAssignableFrom(returnType)) {
            return list;
        } else {
            if (list.size() > 0) {
                return list.get(0);
            }
            return null;
        }
    }
}
