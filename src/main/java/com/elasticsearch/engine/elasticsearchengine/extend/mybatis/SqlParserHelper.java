package com.elasticsearch.engine.elasticsearchengine.extend.mybatis;

import com.google.common.collect.Lists;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.expression.BinaryExpression;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.Parenthesis;
import net.sf.jsqlparser.parser.CCJSqlParserManager;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.select.GroupByElement;
import net.sf.jsqlparser.statement.select.OrderByElement;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.Select;

import java.io.StringReader;
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

        plain.setFromItem(new Table("123"));
        plain.setJoins(Lists.newArrayList());
        setWhereItem(plain.getWhere());
        GroupByElement groupBy = plain.getGroupBy();
        List<Expression> groupByExpressions = groupBy.getGroupByExpressions();
        groupByExpressions.forEach(item -> {
            if (item instanceof Column) {
                Column groupColumn = (Column) item;
                groupColumn.setTable(new Table());
            }
        });

        List<OrderByElement> orderByElements = plain.getOrderByElements();
        orderByElements.forEach(item -> {
            Expression expression = item.getExpression();
            if (expression instanceof Column) {
                Column groupColumn = (Column) expression;
                groupColumn.setTable(new Table());
            }
        });
        return select;
    }

    /**
     * 设置替换where里面的字段
     *
     * @param
     * @return
     */
    public static void setWhereItem(Expression where) {
        if (where instanceof BinaryExpression) {
            BinaryExpression binaryExpression = (BinaryExpression) where;
            Expression rightExpression = binaryExpression.getRightExpression() instanceof Parenthesis ? ((Parenthesis) binaryExpression.getRightExpression()).getExpression() : binaryExpression.getRightExpression();
            Expression leftExpression = binaryExpression.getLeftExpression() instanceof Parenthesis ? ((Parenthesis) binaryExpression.getLeftExpression()).getExpression() : binaryExpression.getLeftExpression();
            if (rightExpression instanceof Column) {
                Column rightColumn = (Column) rightExpression;
                //清除表别名
                rightColumn.setTable(new Table());
            } else {
                setWhereItem(rightExpression);
            }
            if (leftExpression instanceof Column) {
                Column leftColumn = (Column) leftExpression;
                //清除表别名
                leftColumn.setTable(new Table());
            } else {
                setWhereItem(leftExpression);
            }
        }
    }


}
