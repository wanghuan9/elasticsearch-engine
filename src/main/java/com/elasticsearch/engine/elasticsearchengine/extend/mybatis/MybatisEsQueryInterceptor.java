package com.elasticsearch.engine.elasticsearchengine.extend.mybatis;

import com.elasticsearch.engine.elasticsearchengine.common.parse.sql.SqlParamParseHelper;
import com.elasticsearch.engine.elasticsearchengine.common.proxy.handler.exannotation.AnnotationQueryCommon;
import com.elasticsearch.engine.elasticsearchengine.common.queryhandler.sql.EsSqlExecuteHandler;
import com.elasticsearch.engine.elasticsearchengine.model.annotion.EsQuery;
import lombok.extern.slf4j.Slf4j;
import net.sf.jsqlparser.statement.select.Select;
import org.apache.ibatis.cache.CacheKey;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.util.List;
import java.util.Objects;
import java.util.Properties;

/**
 * @author wanghuan
 * @description: mybatis  切es查询拦截器
 * @date 2022-05-10 21:59
 */
@Component
@Intercepts(
        {
                @Signature(type = StatementHandler.class, method = "prepare", args = {Connection.class, Integer.class}),
                @Signature(type = StatementHandler.class, method = "getBoundSql", args = {}),
                @Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class}),
                @Signature(type = Executor.class, method = "query", args = {MappedStatement.class, Object.class, RowBounds.class, ResultHandler.class, CacheKey.class, BoundSql.class}),
        }
)
@Slf4j
public class MybatisEsQueryInterceptor implements Interceptor {


    @Resource
    private EsSqlExecuteHandler esSqlExecuteHandler;

    @Override
    public Object intercept(Invocation invocation) throws Throwable {
        Object target = invocation.getTarget();
        Object[] args = invocation.getArgs();
        if (target instanceof Executor) {
            final Executor executor = (Executor) target;
            Object parameter = args[1];
            boolean isUpdate = args.length == 2;
            MappedStatement ms = (MappedStatement) args[0];
            if (!isUpdate && ms.getSqlCommandType() == SqlCommandType.SELECT) {
                RowBounds rowBounds = (RowBounds) args[2];
                ResultHandler resultHandler = (ResultHandler) args[3];
                BoundSql boundSql;
                if (args.length == 4) {
                    boundSql = ms.getBoundSql(parameter);
                } else {
                    // 几乎不可能走进这里面,除非使用Executor的代理对象调用query[args[6]]
                    boundSql = (BoundSql) args[5];
                }
                //处理ES逻辑
                return esQuery(ms, boundSql);
            }
        }
        return invocation.proceed();
    }

    @Override
    public Object plugin(Object target) {
        if (target instanceof Executor || target instanceof StatementHandler) {
            return Plugin.wrap(target, this);
        }
        return target;
    }

    @Override
    public void setProperties(Properties properties) {
    }

    /**
     * 处理ES逻辑
     *
     * @param ms
     * @param boundSql
     * @return
     * @throws Exception
     */
    private List<?> esQuery(MappedStatement ms, BoundSql boundSql) throws Exception {
        // 获取节点的配置
        Configuration configuration = ms.getConfiguration();
        //获取对应拦截Mapper类,
        Class<?> classType = Class.forName(ms.getId().substring(0, ms.getId().lastIndexOf(".")));
        //获取对应拦截方法名，获取方法名
        String mName = ms.getId().substring(ms.getId().lastIndexOf(".") + 1);
        //反射获取实体类中所有方法（给加了注解的方法上加一些附加信息，classId=1）
        Method[] methods = classType.getDeclaredMethods();
        for (Method method : methods) {
            //判断当前方法上是否有注解
            if (method.isAnnotationPresent(EsQuery.class) && method.getName().equals(mName)) {
                return doQueryEs(method, boundSql, configuration);
            }
        }
        return null;
    }

    /**
     * 执行es查询
     *
     * @param method
     * @param boundSql
     * @param configuration
     * @return
     * @throws Exception
     */
    private List<?> doQueryEs(Method method, BoundSql boundSql, Configuration configuration) throws Exception {
        List<?> result;
        //方法返回值
        Class<?> returnType = method.getReturnType();
        //方法返回值的泛型
        Class<?> returnGenericType = AnnotationQueryCommon.getReturnGenericType(method);
        log.info("原始sql: {}", boundSql.getSql());
        //改写sql
        Select select = SqlParserHelper.rewriteSql(method, boundSql.getSql());
        //通过反射修改sql语句
        Field field = boundSql.getClass().getDeclaredField("sql");
        field.setAccessible(true);
        field.set(boundSql, select.toString());
        log.info("改写后sql: {}", boundSql.getSql());
        //参数替换
        String s = SqlParamParseHelper.paramParse(configuration, boundSql);
        log.info("替换参数后sql: {}", s);
        //执行ES查询
        if (List.class.isAssignableFrom(returnType) && Objects.nonNull(returnGenericType)) {
            result = esSqlExecuteHandler.queryBySql(s, returnGenericType);
        } else {
            result = esSqlExecuteHandler.queryBySql(s, returnType);
        }

        return result;
    }
}
