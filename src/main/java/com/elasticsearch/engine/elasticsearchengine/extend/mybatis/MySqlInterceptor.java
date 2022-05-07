package com.elasticsearch.engine.elasticsearchengine.extend.mybatis;

import com.elasticsearch.engine.elasticsearchengine.common.queryhandler.sql.EsSqlExecuteHandler;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.parser.CCJSqlParserManager;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.Select;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.reflection.DefaultReflectorFactory;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.reflection.SystemMetaObject;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.StringReader;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.util.Properties;

/**
 * Created with IDEA
 * Date:2020/8/26
 * Time:上午9:37
 *
 * @author:lianhui.he
 */
@Component
@Intercepts({
        @Signature(
                type = StatementHandler.class, method = "prepare", args = {Connection.class, Integer.class
        })
})
public class MySqlInterceptor implements Interceptor {

    @Resource
    private EsSqlExecuteHandler esSqlExecuteHandler;

    @Override

    public Object intercept(Invocation invocation) throws Throwable {

        StatementHandler statementHandler = (StatementHandler) invocation.getTarget();
        MetaObject metaObject = MetaObject.forObject(statementHandler, SystemMetaObject.DEFAULT_OBJECT_FACTORY, SystemMetaObject.DEFAULT_OBJECT_WRAPPER_FACTORY, new DefaultReflectorFactory());
        //先拦截到RoutingStatementHandler，里面有个StatementHandler类型的delegate变量，其实现类是BaseStatementHandler，然后就到BaseStatementHandler的成员变量mappedStatement
        MappedStatement mappedStatement = (MappedStatement) metaObject.getValue("delegate.mappedStatement");
        //id为执行的mapper方法的全路径名，如com.yiyezhiqiu.annotation.mapper.StudentMapper.findStudentByClassId
        String id = mappedStatement.getId();
        // com.yiyezhiqiu.annotation.mapper.StudentMapper.findStudentByClassId
        //sql语句类型 select、delete、insert、update
        String sqlCommandType = mappedStatement.getSqlCommandType().toString();
        // SELECT
        BoundSql boundSql = statementHandler.getBoundSql();
        //获取到原始sql语句
        String sql = boundSql.getSql();
        // select * from student s,class c where s.class_id = c.id
        System.out.println("sql:" + sql);
        //获取对应拦截Mapper类,
        Class<?> classType = Class.forName(mappedStatement.getId().substring(0, mappedStatement.getId().lastIndexOf(".")));
        //获取对应拦截方法名，获取方法名
        String mName = mappedStatement.getId().substring(mappedStatement.getId().lastIndexOf(".") + 1, mappedStatement.getId().length());
        //反射获取实体类中所有方法（给加了注解的方法上加一些附加信息，classId=1）
        Method[] methods = classType.getDeclaredMethods();
        for (Method method : methods) {
            //判断当前方法上是否有注解，并且是和执行的方法名一致，则修改sql
//            if (method.isAnnotationPresent(DataPermission.class) && method.getName().equals(mName)) {
//                DataPermission dataPermission = method.getAnnotation(DataPermission.class);
//                if (dataPermission.permission()) {
//                    //执行修改sql
//                    Select select = processSql(sql);
//                    //替换掉了原始的sql语句
//                    metaObject.setValue("delegate.boundSql.sql", select.toString());
//                }
//            }
        }

        return invocation.proceed();
    }

    @Override
    public Object plugin(Object target) {
        if (target instanceof StatementHandler) {
            return Plugin.wrap(target, this);
        } else {
            return target;
        }
    }

    @Override
    public void setProperties(Properties properties) {

    }

    private Select processSql(String oldSql) throws JSQLParserException {
        StringBuffer whereSql = new StringBuffer();
        // 获得原始sql语句
        CCJSqlParserManager parserManager = new CCJSqlParserManager();
        Select select = (Select) parserManager.parse(new StringReader(oldSql));
        PlainSelect plain = (PlainSelect) select.getSelectBody();
        /**
         * 当from后面有别名要获取别名
         */
        String mainTable = null;
        // 获取from后面表名
        mainTable = ((Table) plain.getFromItem()).getName().replace("`", "");
        String mainTableAlias = mainTable;
        System.out.println("mainTable:" + mainTable);
        try {//当有别名时就获得，没有别名则不做异常处理。
            mainTableAlias = plain.getFromItem().getAlias().getName();
        } catch (Exception e) {
            //无别名
        }

        //增加自己想要添加的sql语句的逻辑部分处理
        whereSql.append("1=1 and " + mainTableAlias + ".class_id = 1");
        //原始sql的语句where后面内容
        Expression where = plain.getWhere();
        //当原始where后面的sql条件是空，则直接封装成表达式
        if (where == null) {
            if (whereSql.length() > 0) {
                Expression expression = CCJSqlParserUtil
                        .parseCondExpression(whereSql.toString());
                Expression whereExpression = (Expression) expression;
                plain.setWhere(whereExpression);
            }
        } else {//当原始where后面不为空，重新拼接
            if (whereSql.length() > 0) {
                //where条件之前存在，需要重新进行拼接
                whereSql.append(" and ( " + where.toString() + " )");
            } else {
                //新增片段不存在，使用之前的sql
                whereSql.append(where.toString());
            }
            Expression expression = CCJSqlParserUtil
                    .parseCondExpression(whereSql.toString());
            plain.setWhere(expression);
        }
        return select;
    }
}