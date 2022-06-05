package com.elasticsearch.engine.common.parse.sql;

import com.elasticsearch.engine.GlobalConfig;
import com.elasticsearch.engine.common.utils.CaseFormatUtils;
import com.elasticsearch.engine.model.annotion.EsQueryIndex;
import com.elasticsearch.engine.model.exception.EsHelperQueryException;
import com.google.common.collect.Lists;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.expression.*;
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
        setSelectItem(plain, isCleanAs);
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
     * 设置替换select 里面的字段
     *
     * @param plainSelect
     * @return
     */
    public static void setSelectItem(PlainSelect plainSelect, Boolean isCleanAs) {
        for (SelectItem selectItem : plainSelect.getSelectItems()) {
            selectItem.accept(new SelectItemVisitorAdapter() {
                @Override
                public void visit(SelectExpressionItem item) {
                    if (item.getExpression() instanceof Function) {
                        setFunction((Function) item.getExpression());
                    } else if (item.getExpression() instanceof CaseExpression) {
                        setCaseExpression((CaseExpression) item.getExpression());
                    } else if (item.getExpression() instanceof Column) {
                        Column column = (Column) item.getExpression();
                        //清除t.
                        reNameColumnName(column);
                        column.setTable(new Table());
                    }
                    //清除as
                    if (isCleanAs) {
                        item.setAlias(null);
                    }
                }
            });
        }
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
        if (where instanceof Parenthesis) {
            Parenthesis inExpression = (Parenthesis) where;
            where = inExpression.getExpression();
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
        } else if (rightExpression instanceof Function) {
            setFunction((Function) rightExpression);
        } else {
            setWhereItem(rightExpression);
        }
        if (leftExpression instanceof Column) {
            Column leftColumn = (Column) leftExpression;
            reNameColumnName(leftColumn);
            //清除表别名
            leftColumn.setTable(new Table());
        } else if (leftExpression instanceof Function) {
            setFunction((Function) leftExpression);
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
     * 设置替换select 中 function里面的字段
     *
     * @param function
     */
    private static void setFunction(Function function) {
        if (function.getParameters() == null || function.getParameters().getExpressions() == null) {
            return;
        }
        List<Expression> list = function.getParameters().getExpressions();
        list.forEach(data -> {
            if (data instanceof Function) {
                setFunction((Function) data);
            } else if (data instanceof Column) {
                Column column = (Column) data;
                reNameColumnName(column);
                //清除表别名
                column.setTable(new Table());
            }
        });
    }

    /**
     * 设置替换 select里面CaseExpression里面的字段
     *
     * @param caseExpression
     * @return
     */
    public static void setCaseExpression(CaseExpression caseExpression) {
        if (caseExpression.getWhenClauses() == null) {
            return;
        }
        List<WhenClause> list = caseExpression.getWhenClauses();
        list.forEach(data -> {
            if (data instanceof WhenClause) {
                setWhereItem(((WhenClause) data).getWhenExpression());
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
