package com.elasticsearch.engine.elasticsearchengine.extend.jpa;

import com.elasticsearch.engine.elasticsearchengine.common.parse.sql.SqlParamParseHelper;
import com.elasticsearch.engine.elasticsearchengine.common.proxy.handler.exannotation.AnnotationQueryCommon;
import com.elasticsearch.engine.elasticsearchengine.common.queryhandler.sql.EsSqlExecuteHandler;
import com.elasticsearch.engine.elasticsearchengine.extend.mybatis.SqlParserHelper;
import com.elasticsearch.engine.elasticsearchengine.model.emenu.SqlParamParse;
import com.elasticsearch.engine.elasticsearchengine.model.exception.EsHelperJpaExecuteException;
import lombok.extern.slf4j.Slf4j;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.statement.select.Select;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Objects;


/**
 * @author wanghuan
 * @description: LogAop
 * @date 2022-05-24 23:17
 */
@Slf4j
@Component
@Aspect
public class LogAop {
    @Resource
    private EsSqlExecuteHandler esSqlExecuteHandler;
    
    // 自己定义切点 拦截重试模方法
    @Pointcut("@annotation(com.elasticsearch.engine.elasticsearchengine.model.annotion.EsQuery)")
    public void esQueryCut() {
    }

    @Around(value = "esQueryCut()")
    public Object retryAdvice(ProceedingJoinPoint pjp) throws Throwable {
        MethodSignature signature = (MethodSignature) pjp.getSignature();
        Method method = signature.getMethod();
        Object[] args = pjp.getArgs();
        try {
            pjp.proceed(args);
        } catch (EsHelperJpaExecuteException e) {
           return esQuery(method,e,args);
        } 
        //后处理
        return null;
    }

    /**
     * es查询
     * @param method
     * @param e
     * @param args
     * @return
     * @throws JSQLParserException
     */
    private Object esQuery(Method method,EsHelperJpaExecuteException e,Object[] args) throws JSQLParserException {
        String sql = e.getMessage();
        log.info("原始sql: {}", sql);
        //改写sql
        Select select = SqlParserHelper.rewriteSql(method, sql);
        log.info("改写后sql: {}", select);
        //参数替换
        // 解析sql参数
        String paramSql = SqlParamParseHelper.getMethodArgsSql(select.toString(), method, args, SqlParamParse.JAP_SQL_PARAM);
        log.info("替换参数后sql: {}", paramSql);
        //执行ES查询
       return doQueryEs(paramSql,method);
    }

    /**
     * 执行es查询
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
            list = esSqlExecuteHandler.queryBySql(sql, returnGenericType);
        } else {
            list = esSqlExecuteHandler.queryBySql(sql, returnType);
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
