package com.elasticsearch.engine.elasticsearchengine.extend.mybatis;

import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.expression.BinaryExpression;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.Function;
import net.sf.jsqlparser.expression.Parenthesis;
import net.sf.jsqlparser.parser.CCJSqlParserManager;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.delete.Delete;
import net.sf.jsqlparser.statement.insert.Insert;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.statement.select.SelectItem;
import net.sf.jsqlparser.statement.select.WithItem;
import net.sf.jsqlparser.statement.update.Update;
import net.sf.jsqlparser.util.TablesNamesFinder;
import net.sf.jsqlparser.util.deparser.ExpressionDeParser;
import net.sf.jsqlparser.util.deparser.InsertDeParser;
import net.sf.jsqlparser.util.deparser.SelectDeParser;
import net.sf.jsqlparser.util.deparser.StatementDeParser;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.type.TypeHandlerRegistry;

import java.io.StringReader;
import java.text.DateFormat;
import java.util.*;
import java.util.regex.Matcher;

/**
 * @author wanghuan
 * @description: ROOD
 * @date 2022-05-08 20:59
 */
public class SqlParserHelper {

    /**
     * sql改写
     *
     * @param oldSql
     * @return
     * @throws JSQLParserException
     */
    public static Select processSql(String oldSql) throws JSQLParserException {
        StringBuffer whereSql = new StringBuffer();
        // 获得原始sql语句
        CCJSqlParserManager parserManager = new CCJSqlParserManager();
        Select select = (Select) parserManager.parse(new StringReader(oldSql));
        PlainSelect plain = (PlainSelect) select.getSelectBody();
        TablesNamesFinder tablesNamesFinder = new TablesNamesFinder();
        List<String> tableList = tablesNamesFinder.getTableList(select);


        //解析SQL --> 从String 解析为AST statement
        Statement statement = CCJSqlParserUtil.parse(oldSql);

        //遍历 -->
        StringBuilder buffer = new StringBuilder();
        statement.accept(new ShadowStatementDeParser(buffer));

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

        String s = plain.getSelectItems().toString();
        //增加自己想要添加的sql语句的逻辑部分处理
        whereSql.append("1=1 and " + mainTableAlias + ".class_id = 1");
        //原始sql的语句where后面内容
        Expression where = plain.getWhere();
        Map<String, String> map = new HashMap<>();
        Set<String> stringSet = new HashSet<>();
        getWhereItem(where, stringSet);
        for (String sss : stringSet) {
            map.put(sss, sss);
        }
        setWhereItem(where, map);


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

        Select test = test(oldSql);
        String s1 = DruidShadowHelper.generateShadowSql(oldSql);
        return select;
    }

    /**
     * 对sql中的？进行参数替换
     *
     * @param configuration
     * @param boundSql
     * @return
     */
    public static String showSql(Configuration configuration, BoundSql boundSql) {
        // 获取参数
        Object parameterObject = boundSql.getParameterObject();
        List<ParameterMapping> parameterMappings = boundSql
                .getParameterMappings();
        // sql语句中多个空格都用一个空格代替
        String sql = boundSql.getSql().replaceAll("[\\s]+", " ");
        if (!CollectionUtils.isEmpty(parameterMappings) && parameterObject != null) {
            // 获取类型处理器注册器，类型处理器的功能是进行java类型和数据库类型的转换<br>　　　　　　　// 如果根据parameterObject.getClass(）可以找到对应的类型，则替换
            TypeHandlerRegistry typeHandlerRegistry = configuration.getTypeHandlerRegistry();
            if (typeHandlerRegistry.hasTypeHandler(parameterObject.getClass())) {
                sql = sql.replaceFirst("\\?", Matcher.quoteReplacement(getParameterValue(parameterObject)));

            } else {
                // MetaObject主要是封装了originalObject对象，提供了get和set的方法用于获取和设置originalObject的属性值,主要支持对JavaBean、Collection、Map三种类型对象的操作
                MetaObject metaObject = configuration.newMetaObject(parameterObject);
                for (ParameterMapping parameterMapping : parameterMappings) {
                    String propertyName = parameterMapping.getProperty();
                    if (metaObject.hasGetter(propertyName)) {
                        Object obj = metaObject.getValue(propertyName);
                        sql = sql.replaceFirst("\\?", Matcher.quoteReplacement(getParameterValue(obj)));
                    } else if (boundSql.hasAdditionalParameter(propertyName)) {
                        // 该分支是动态sql
                        Object obj = boundSql.getAdditionalParameter(propertyName);
                        sql = sql.replaceFirst("\\?", Matcher.quoteReplacement(getParameterValue(obj)));

                    } else {
                        //打印出缺失，提醒该参数缺失并防止错位
                        sql = sql.replaceFirst("\\?", "缺失");
                    }
                }
            }
        }
        return sql;
    }

    /**
     * 如果参数是String，则添加单引号， 如果是日期，则转换为时间格式器并加单引号； 对参数是null和不是null的情况作了处理
     *
     * @param obj
     * @return
     */
    private static String getParameterValue(Object obj) {
        String value = null;
        if (obj instanceof String) {
            value = "'" + obj.toString() + "'";
        } else if (obj instanceof Date) {
            DateFormat formatter = DateFormat.getDateTimeInstance(DateFormat.DEFAULT, DateFormat.DEFAULT, Locale.CHINA);
            value = "'" + formatter.format(new Date()) + "'";
        } else {
            if (obj != null) {
                value = obj.toString();
            } else {
                value = "";
            }
        }
        return value;
    }

    public static Select buildSelectFromTableAndSelectItems(Table table, SelectItem... selectItems) {
        Select select = new Select();
        PlainSelect body = new PlainSelect();
        body.addSelectItems(selectItems);
        body.setFromItem(table);
        select.setSelectBody(body);
        return select;
    }

    private static Select test(String oldSql) throws JSQLParserException {
        StringBuffer whereSql = new StringBuffer();
        // 获得原始sql语句
        CCJSqlParserManager parserManager = new CCJSqlParserManager();
        Select select = (Select) parserManager.parse(new StringReader(oldSql));
        select.getSelectBody();
        PlainSelect plain = (PlainSelect) select.getSelectBody();
        Table table = new Table("123");
        PlainSelect body = new PlainSelect();
        body.addSelectItems(plain.getSelectItems());
        body.setFromItem(table);
        select.setSelectBody(body);
        return select;
    }

    public static void changeTableName(Table table) {
        if (StringUtils.isNotBlank(table.getName())) {
            table.setName(table.getName() + "123");
        }
    }

    /**
     * 获取where里面的字段
     *
     * @param
     * @return
     */
    public static void getWhereItem(Expression where, Set<String> tblNameSet) {
        if (where instanceof BinaryExpression) {
            BinaryExpression binaryExpression = (BinaryExpression) where;
            Expression rightExpression = binaryExpression.getRightExpression() instanceof Parenthesis ? ((Parenthesis) binaryExpression.getRightExpression()).getExpression() : binaryExpression.getRightExpression();
            Expression leftExpression = binaryExpression.getLeftExpression() instanceof Parenthesis ? ((Parenthesis) binaryExpression.getLeftExpression()).getExpression() : binaryExpression.getLeftExpression();
            if (rightExpression instanceof Column) {
                Column rightColumn = (Column) rightExpression;
                tblNameSet.add(rightColumn.getColumnName());
            }
            if (rightExpression instanceof Function) {
                getFunction((Function) rightExpression, tblNameSet);
            } else {
                getWhereItem(rightExpression, tblNameSet);
            }
            if (leftExpression instanceof Column) {
                Column leftColumn = (Column) leftExpression;
                tblNameSet.add(leftColumn.getColumnName());
            }
            if (leftExpression instanceof Function) {
                getFunction((Function) leftExpression, tblNameSet);
            } else {
                getWhereItem(leftExpression, tblNameSet);
            }
        } else if (where instanceof Parenthesis) {
            getWhereItem(((Parenthesis) where).getExpression(), tblNameSet);
        }
    }

    /**
     * 获取select里面function里面的字段
     *
     * @param function
     * @param selectItemSet
     * @return
     */
    public static void getFunction(Function function, Set<String> selectItemSet) {
        if (function.getParameters() == null || function.getParameters().getExpressions() == null) {
            return;
        }
        List<Expression> list = function.getParameters().getExpressions();
        list.forEach(data -> {
            if (data instanceof Function) {
                getFunction((Function) data, selectItemSet);
            } else if (data instanceof Column) {
                Column column = (Column) data;
                selectItemSet.add(column.getColumnName());
            } else {
                getWhereItem(data, selectItemSet);
            }
        });

    }

    /**
     * 设置替换where里面的字段
     *
     * @param
     * @return
     */
    public static void setWhereItem(Expression where, Map<String, String> map) {
        if (where instanceof BinaryExpression) {
            BinaryExpression binaryExpression = (BinaryExpression) where;
            Expression rightExpression = binaryExpression.getRightExpression() instanceof Parenthesis ? ((Parenthesis) binaryExpression.getRightExpression()).getExpression() : binaryExpression.getRightExpression();
            Expression leftExpression = binaryExpression.getLeftExpression() instanceof Parenthesis ? ((Parenthesis) binaryExpression.getLeftExpression()).getExpression() : binaryExpression.getLeftExpression();
            if (rightExpression instanceof Column) {
                Column rightColumn = (Column) rightExpression;
                rightColumn.setColumnName(Optional.ofNullable(map.get(rightColumn.getColumnName())).orElse(rightColumn.getColumnName()));
            } else if (rightExpression instanceof Function) {
                setFunction((Function) rightExpression, map);
            } else {
                setWhereItem(rightExpression, map);
            }
            if (leftExpression instanceof Column) {
                Column leftColumn = (Column) leftExpression;
                leftColumn.setTable(new Table());
                leftColumn.setColumnName(Optional.ofNullable(map.get(leftColumn.getColumnName())).orElse(leftColumn.getColumnName()));
            } else if (leftExpression instanceof Function) {
                setFunction((Function) leftExpression, map);
            } else {
                setWhereItem(leftExpression, map);
            }
        }
    }

    /**
     * 设置替换select 中 function里面的字段
     *
     * @param function
     * @param map
     * @return
     */
    public static void setFunction(Function function, Map<String, String> map) {
        if (function.getParameters() == null || function.getParameters().getExpressions() == null) {
            return;
        }
        List<Expression> list = function.getParameters().getExpressions();
        list.forEach(data -> {
            if (data instanceof Function) {
                setFunction((Function) data, map);
            } else if (data instanceof Column) {
                Column column = (Column) data;
                column.setColumnName(Optional.ofNullable(map.get(column.getColumnName())).orElse(column.getColumnName()));
            } else {
                setWhereItem(data, map);
            }
        });

    }

    public static class ShadowStatementDeParser extends StatementDeParser {

        public ShadowStatementDeParser(StringBuilder buffer) {
            super(buffer);
        }

        @Override
        public void visit(Select select) {
            ShadowSelectDeParser selectDeParser = new ShadowSelectDeParser();
            selectDeParser.setBuffer(getBuffer());
            ExpressionDeParser expressionDeParser = new ExpressionDeParser(selectDeParser, getBuffer());
            selectDeParser.setExpressionVisitor(expressionDeParser);
            if (select.getWithItemsList() != null && !select.getWithItemsList().isEmpty()) {
                getBuffer().append("WITH ");
                for (Iterator<WithItem> iter = select.getWithItemsList().iterator(); iter.hasNext(); ) {
                    WithItem withItem = iter.next();
                    withItem.accept(selectDeParser);
                    if (iter.hasNext()) {
                        getBuffer().append(",");
                    }
                    getBuffer().append(" ");
                }
            }
            select.getSelectBody().accept(selectDeParser);
        }

        @Override
        public void visit(Insert insert) {
            //修改tableName
            changeTableName(insert.getTable());

            ShadowSelectDeParser selectDeParser = new ShadowSelectDeParser();
            selectDeParser.setBuffer(getBuffer());
            ExpressionDeParser expressionDeParser = new ExpressionDeParser(selectDeParser, getBuffer());
            selectDeParser.setExpressionVisitor(expressionDeParser);
            InsertDeParser insertDeParser = new InsertDeParser(expressionDeParser, selectDeParser, getBuffer());
            insertDeParser.deParse(insert);
        }

        @Override
        public void visit(Update update) {
        }

        @Override
        public void visit(Delete delete) {
        }
    }

    private static class ShadowSelectDeParser extends SelectDeParser {
        @Override
        public void visit(Table tableName) {
            changeTableName(tableName);
            super.visit(tableName);
        }
    }

}
