/*
 * Copyright (c) 2011-2022, baomidou (jobob@qq.com).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.elasticsearch.engine.elasticsearchengine.extend.mybatis;

import com.elasticsearch.engine.elasticsearchengine.common.proxy.handler.exannotation.AnnotationQueryCommon;
import com.elasticsearch.engine.elasticsearchengine.common.queryhandler.sql.EsSqlExecuteHandler;
import com.elasticsearch.engine.elasticsearchengine.model.annotion.EsQuery;
import net.sf.jsqlparser.JSQLParserException;
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
import java.lang.reflect.Method;
import java.sql.Connection;
import java.util.List;
import java.util.Objects;
import java.util.Properties;

/**
 * @author miemie
 * @since 3.4.0
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
public class MybatisPlusInterceptor implements Interceptor {


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
                System.out.println("sql:" + boundSql.getSql());
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

    private List<?> esQuery(MappedStatement ms, BoundSql boundSql) throws ClassNotFoundException, JSQLParserException {
        // 获取节点的配置
        Configuration configuration = ms.getConfiguration();
        //获取对应拦截Mapper类,
        Class<?> classType = Class.forName(ms.getId().substring(0, ms.getId().lastIndexOf(".")));
        //获取对应拦截方法名，获取方法名
        String mName = ms.getId().substring(ms.getId().lastIndexOf(".") + 1, ms.getId().length());
        //反射获取实体类中所有方法（给加了注解的方法上加一些附加信息，classId=1）
        Method[] methods = classType.getDeclaredMethods();
        List<?> list = null;
        for (Method method : methods) {
            //方法返回值
            Class<?> returnType = method.getReturnType();
            //方法返回值的泛型
            Class<?> returnGenericType = AnnotationQueryCommon.getReturnGenericType(method);
            //判断当前方法上是否有注解
            if (method.isAnnotationPresent(EsQuery.class) && method.getName().equals(mName)) {
                EsQuery esQuery = method.getAnnotation(EsQuery.class);
                //改写sql
                Select select = SqlParserHelper.processSql(boundSql.getSql());
                System.out.println(select.toString());
                //参数替换
                String s = SqlParserHelper.showSql(configuration, boundSql);
                //执行ES查询
                if (List.class.isAssignableFrom(returnType) && Objects.nonNull(returnGenericType)) {
                    list = esSqlExecuteHandler.queryBySQL(s, returnGenericType);
                } else {
                    list = esSqlExecuteHandler.queryBySQL(s, returnType);
                }
            }
        }
        return list;
    }
}
