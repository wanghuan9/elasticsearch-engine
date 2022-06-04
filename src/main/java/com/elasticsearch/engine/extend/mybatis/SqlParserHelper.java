package com.elasticsearch.engine.extend.mybatis;

import com.elasticsearch.engine.GlobalConfig;
import com.elasticsearch.engine.common.utils.CaseFormatUtils;
import com.elasticsearch.engine.model.annotion.EsQueryIndex;
import com.elasticsearch.engine.model.exception.EsHelperQueryException;
import com.google.common.collect.Lists;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.expression.BinaryExpression;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.Parenthesis;
import net.sf.jsqlparser.expression.operators.relational.InExpression;
import net.sf.jsqlparser.parser.CCJSqlParserManager;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.select.*;
import org.apache.commons.collections4.CollectionUtils;

import java.io.StringReader;
import java.lang.reflect.Method;
import java.util.List;

/**
 * @author wanghuan
 * @description: ROOD
 * @date 2022-05-08 20:59
 */
public class SqlParserHelper {

    /**
     * sql改写
     *
     * @param method
     * @param oldSql
     * @param isCleanAs 是否清除as别名
     * @return
     * @throws JSQLParserException
     */
    public static Select rewriteSql(Method method, String oldSql, Boolean isCleanAs) throws JSQLParserException {
        // 获得原始sql语句
        CCJSqlParserManager parserManager = new CCJSqlParserManager();
        Select select = (Select) parserManager.parse(new StringReader(oldSql));
        PlainSelect plain = (PlainSelect) select.getSelectBody();
        //替换表名
        setTableItem(method, plain);
        //清除关联
        plain.setJoins(Lists.newArrayList());
        //from 别名清除
        setSelectItem(plain.getSelectItems(), isCleanAs);
        //where 别名清除
        setWhereItem(plain.getWhere());
        //group by 别名清除
        setGroupItem(plain.getGroupBy());
        //having
        setHavingItem(plain.getHaving());
        //order by 别名清除
        setOrderItem(plain.getOrderByElements());
        return select;
    }

    /**
     * TODO 获取索引名的方法抽取通用方法
     * 替换表名
     *
     * @param method
     * @param plain
     */
    private static void setTableItem(Method method, PlainSelect plain) {
        Class<?> clazz = method.getDeclaringClass();
        EsQueryIndex ann = clazz.getAnnotation(EsQueryIndex.class);
        if (ann == null) {
            throw new EsHelperQueryException("undefine query-index @EsQueryIndex");
        }
        plain.setFromItem(new Table(ann.value()));
    }

    /**
     * select 改写
     *
     * @param selectItems
     */
    public static void setSelectItem(List<SelectItem> selectItems, Boolean isCleanAs) {
        if (CollectionUtils.isEmpty(selectItems)) {
            return;
        }
        selectItems.forEach(item -> {
            if (item instanceof SelectExpressionItem) {
                SelectExpressionItem selectColumn = (SelectExpressionItem) item;
                //清除t.
                Expression expression = selectColumn.getExpression();
                if (expression instanceof Column) {
                    Column groupColumn = (Column) expression;
                    reNameColumnName(groupColumn);
                    groupColumn.setTable(new Table());
                }
                //清除as
                if (isCleanAs) {
                    selectColumn.setAlias(null);
                }
            }
        });
    }

    /**
     * 设置替换where里面的字段
     *
     * @param
     * @return
     */
    public static void setWhereItem(Expression where) {
        if (where == null) {
            return;
        }
        Expression rightExpression = null;
        Expression leftExpression = null;
        if (where instanceof BinaryExpression) {
            BinaryExpression binaryExpression = (BinaryExpression) where;
            rightExpression = binaryExpression.getRightExpression() instanceof Parenthesis ? ((Parenthesis) binaryExpression.getRightExpression()).getExpression() : binaryExpression.getRightExpression();
            leftExpression = binaryExpression.getLeftExpression() instanceof Parenthesis ? ((Parenthesis) binaryExpression.getLeftExpression()).getExpression() : binaryExpression.getLeftExpression();

        }
        if (where instanceof InExpression) {
            InExpression inExpression = (InExpression) where;
            rightExpression = inExpression.getRightExpression() instanceof Parenthesis ? ((Parenthesis) inExpression.getRightExpression()).getExpression() : inExpression.getRightExpression();
            leftExpression = inExpression.getLeftExpression() instanceof Parenthesis ? ((Parenthesis) inExpression.getLeftExpression()).getExpression() : inExpression.getLeftExpression();
        }
        if (rightExpression instanceof Column) {
            Column rightColumn = (Column) rightExpression;
            reNameColumnName(rightColumn);
            //清除表别名
            rightColumn.setTable(new Table());
        } else {
            setWhereItem(rightExpression);
        }
        if (leftExpression instanceof Column) {
            Column leftColumn = (Column) leftExpression;
            reNameColumnName(leftColumn);
            //清除表别名
            leftColumn.setTable(new Table());
        } else {
            setWhereItem(leftExpression);
        }
    }

    /**
     * 设置替换groupBy里面的字段
     *
     * @param groupBy
     */
    private static void setGroupItem(GroupByElement groupBy) {
        if (groupBy == null) {
            return;
        }
        List<Expression> groupByExpressions = groupBy.getGroupByExpressions();
        groupByExpressions.forEach(item -> {
            if (item instanceof Column) {
                Column groupColumn = (Column) item;
                reNameColumnName(groupColumn);
                groupColumn.setTable(new Table());
            }
        });
    }

    /**
     * 设置替换having里面的字段
     *
     * @param having
     */
    private static void setHavingItem(Expression having) {
        if (having == null) {
            return;
        }
        setWhereItem(having);
    }

    /**
     * 设置替换orderBy里面的字段
     *
     * @param orderByElements
     */
    private static void setOrderItem(List<OrderByElement> orderByElements) {
        if (CollectionUtils.isEmpty(orderByElements)) {
            return;
        }
        orderByElements.forEach(item -> {
            Expression expression = item.getExpression();
            if (expression instanceof Column) {
                Column groupColumn = (Column) expression;
                reNameColumnName(groupColumn);
                groupColumn.setTable(new Table());
            }
        });
    }

    /**
     * ColumnName mysql 下划线 转es驼峰
     *
     * @param column
     */
    private static void reNameColumnName(Column column) {
        if (!GlobalConfig.namingStrategy) {
            String columnName = column.getColumnName();
            columnName = CaseFormatUtils.underscoreToCamel(columnName);
            column.setColumnName(columnName);
        }

    }

}
