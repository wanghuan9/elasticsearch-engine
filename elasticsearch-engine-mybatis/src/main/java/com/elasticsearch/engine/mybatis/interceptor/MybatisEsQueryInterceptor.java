package com.elasticsearch.engine.mybatis.interceptor;

import com.elasticsearch.engine.base.common.parse.sql.SqlParserHelper;
import com.elasticsearch.engine.base.common.proxy.handler.exannotation.AnnotationQueryCommon;
import com.elasticsearch.engine.base.common.queryhandler.sql.EsSqlExecuteHandler;
import com.elasticsearch.engine.base.config.EsEngineConfig;
import com.elasticsearch.engine.base.model.domain.BackDto;
import com.elasticsearch.engine.mybatis.annotion.MybatisEsQuery;
import com.elasticsearch.engine.mybatis.model.MybatisBackDto;
import com.elasticsearch.engine.mybatis.parse.SqlParamParseMybatisHelper;
import lombok.extern.slf4j.Slf4j;
import net.sf.jsqlparser.statement.select.Select;
import org.apache.ibatis.cache.CacheKey;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlCommandType;
import org.apache.ibatis.mapping.SqlSource;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.SystemMetaObject;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Objects;
import java.util.Properties;

/**
* @author wanghuan
* @description mybatis 切es查询拦截器
* @mail 958721894@qq.com       
* @date 2022-05-10 21:59
*/
@Component
@Intercepts(
        {
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
            Object parameter = args[1];
            boolean isUpdate = args.length == 2;
            MappedStatement ms = (MappedStatement) args[0];
            //判断是否包含es查询注解以及全局开关
            Method method = isEsQuery(ms);
            //不包含,并且不是select查询语句直接返回
            if (Objects.nonNull(method) && !isUpdate && ms.getSqlCommandType() == SqlCommandType.SELECT) {
                BoundSql boundSql;
                if (args.length == 4) {
                    boundSql = ms.getBoundSql(parameter);
                } else {
                    // 几乎不可能走进这里面,除非使用Executor的代理对象调用query[args[6]]
                    boundSql = (BoundSql) args[5];
                }
                //获取回表查询参数
                BackDto backDto = MybatisBackDto.hasBack(method);
                //判断是否需要回表查询
                if (Objects.isNull(backDto)) {
                    //无需回表直接执行es查询
                    return doQueryEs(method, boundSql, ms.getConfiguration());
                } else {
                    //需要回表es查询 并将修改后的回表sql绑定到mybatis 
                    MappedStatement mappedStatement = doQueryEsBack(method, boundSql, ms, backDto);
                    args[0] = mappedStatement;
                }
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
     * 判断是否执行es查询
     *
     * @param ms
     * @return
     * @throws Exception
     */
    private Method isEsQuery(MappedStatement ms) throws Exception {
        //获取对应拦截Mapper类,
        Class<?> classType = Class.forName(ms.getId().substring(0, ms.getId().lastIndexOf(".")));
        //获取对应拦截方法名，获取方法名
        String mName = ms.getId().substring(ms.getId().lastIndexOf(".") + 1);
        //反射获取实体类中所有方法（给加了注解的方法上加一些附加信息，classId=1）
        Method[] methods = classType.getDeclaredMethods();
        for (Method method : methods) {
            //判断当前方法上是否有注解
            if (method.isAnnotationPresent(MybatisEsQuery.class) && method.getName().equals(mName)) {
                //不走es查询直接返回
                if(!EsEngineConfig.isEsquery(method)){
                    return null;
                }
                return method;
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
        if (EsEngineConfig.getSqlTraceLog()) {
            log.info("原始sql: {}", boundSql.getSql());
        }
        //改写sql
        Select select = SqlParserHelper.rewriteSql(method, boundSql.getSql(), Boolean.FALSE, null);
        //通过反射修改sql语句
        Field field = boundSql.getClass().getDeclaredField("sql");
        field.setAccessible(true);
        field.set(boundSql, select.toString());
        if (EsEngineConfig.getSqlTraceLog()) {
            log.info("改写后sql: {}", boundSql.getSql());
        }
        //参数替换
        String sql = SqlParamParseMybatisHelper.paramParse(configuration, boundSql);
        if (EsEngineConfig.getSqlTraceLog()) {
            log.info("替换参数后sql: {}", sql);
        }
        //执行ES查询
        if (List.class.isAssignableFrom(returnType) && Objects.nonNull(returnGenericType)) {
            result = esSqlExecuteHandler.queryBySql(sql, returnGenericType, Boolean.TRUE);
        } else {
            result = esSqlExecuteHandler.queryBySql(sql, returnType, Boolean.TRUE);
        }

        return result;
    }


    /**
     * 执行es回表查询
     *
     * @param method
     * @param boundSql
     * @param ms
     * @param backDto
     * @return
     * @throws Exception
     */
    private MappedStatement doQueryEsBack(Method method, BoundSql boundSql, MappedStatement ms, BackDto backDto) throws Exception {
        Configuration configuration = ms.getConfiguration();
        String originalSql = boundSql.getSql();
        if (EsEngineConfig.getSqlTraceLog()) {
            log.info("原始sql: {}", originalSql);
        }
        //改写sql
        Select select = SqlParserHelper.rewriteSql(method, boundSql.getSql(), Boolean.FALSE, backDto);
        //通过反射修改sql语句
        Field field = boundSql.getClass().getDeclaredField("sql");
        field.setAccessible(true);
        field.set(boundSql, select.toString());
        if (EsEngineConfig.getSqlTraceLog()) {
            log.info("改写后sql: {}", boundSql.getSql());
        }
        //参数替换
        String sql = SqlParamParseMybatisHelper.paramParse(configuration, boundSql);
        if (EsEngineConfig.getSqlTraceLog()) {
            log.info("替换参数后sql: {}", sql);
        }
        //执行ES查询
        List<?> esResult = esSqlExecuteHandler.queryBySql(sql, backDto.getBackColumnTyp(), Boolean.TRUE);

        //将原sql改写成回表sql
        String backSql = SqlParserHelper.rewriteBackSql(originalSql, backDto, esResult);
        if (EsEngineConfig.getSqlTraceLog()) {
            log.info("回表sql :  {}", backSql);
        }
        //替换mybatis执行的sql
        MappedStatement qs = newMappedStatement(ms, new BoundSqlSqlSource(boundSql));
        MetaObject msObject = SystemMetaObject.forObject(qs);
        msObject.setValue("sqlSource.boundSql.sql", backSql);
        return qs;
    }

    /**
     * 由于MappedStatement是一个全局共享的对象，因而需要复制一个对象来进行操作，防止并发访问导致错误
     *
     * @param ms
     * @param newSqlSource
     * @return
     */
    private MappedStatement newMappedStatement(MappedStatement ms, SqlSource newSqlSource) throws InstantiationException, IllegalAccessException {
        MappedStatement.Builder builder = new MappedStatement.Builder(ms.getConfiguration(), ms.getId() + "_分页", newSqlSource, ms.getSqlCommandType());
        builder.resource(ms.getResource());
        builder.fetchSize(ms.getFetchSize());
        builder.statementType(ms.getStatementType());
        builder.keyGenerator(ms.getKeyGenerator());
        if (ms.getKeyProperties() != null && ms.getKeyProperties().length != 0) {
            StringBuilder keyProperties = new StringBuilder();
            for (String keyProperty : ms.getKeyProperties()) {
                keyProperties.append(keyProperty).append(",");
            }
            keyProperties.delete(keyProperties.length() - 1, keyProperties.length());
            builder.keyProperty(keyProperties.toString());
        }

        builder.timeout(ms.getTimeout());
        builder.parameterMap(ms.getParameterMap());
        builder.resultMaps(ms.getResultMaps());
        builder.resultSetType(ms.getResultSetType());
        builder.cache(ms.getCache());
        builder.flushCacheRequired(ms.isFlushCacheRequired());
        builder.useCache(ms.isUseCache());
        return builder.build();
    }


    private static class BoundSqlSqlSource implements SqlSource {
        BoundSql boundSql;

        public BoundSqlSqlSource(BoundSql boundSql) {
            this.boundSql = boundSql;
        }

        @Override
        public BoundSql getBoundSql(Object parameterObject) {
            return boundSql;
        }
    }
}
