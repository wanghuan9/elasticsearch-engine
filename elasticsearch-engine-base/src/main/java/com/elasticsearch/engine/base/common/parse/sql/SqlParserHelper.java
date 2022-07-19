package com.elasticsearch.engine.base.common.parse.sql;

import com.elasticsearch.engine.base.common.utils.CaseFormatUtils;
import com.elasticsearch.engine.base.common.utils.LocalStringUtils;
import com.elasticsearch.engine.base.common.utils.ReflectionUtils;
import com.elasticsearch.engine.base.config.EsEngineConfig;
import com.elasticsearch.engine.base.model.annotion.EsQueryIndex;
import com.elasticsearch.engine.base.model.domain.BackDto;
import com.elasticsearch.engine.base.model.exception.EsEngineQueryException;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.expression.*;
import net.sf.jsqlparser.expression.operators.conditional.AndExpression;
import net.sf.jsqlparser.expression.operators.relational.InExpression;
import net.sf.jsqlparser.parser.CCJSqlParserManager;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.select.*;
import net.sf.jsqlparser.util.SelectUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import java.io.StringReader;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.Objects;

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
    public static Select rewriteSql(Method method, String oldSql, Boolean isCleanAs, BackDto backDto) throws JSQLParserException {
        // 获得原始sql语句
        CCJSqlParserManager parserManager = new CCJSqlParserManager();
        Select select = (Select) parserManager.parse(new StringReader(oldSql));
        PlainSelect plain = (PlainSelect) select.getSelectBody();
        Map<String, String> tableNames = getJoinTableName(plain);
        Map<String, String> tableAlias = ReflectionUtils.getEsAlias(method, tableNames);
        //替换表名
        setTableItem(method, plain);
        //清除关联
        plain.setJoins(Lists.newArrayList());
        //from 别名清除
        if (Objects.isNull(backDto)) {
            setSelectItem(plain, isCleanAs, tableAlias);
        } else {
            setSelectItem(select, backDto);
        }
        //where 别名清除
        setWhereItem(plain.getWhere(), tableAlias);
        //group by 别名清除
        setGroupItem(plain.getGroupBy(), tableAlias);
        //having
        setHavingItem(plain.getHaving(), tableAlias);
        //order by 别名清除
        setOrderItem(plain.getOrderByElements(), tableAlias);
        return select;
    }

    /**
     * 回表sql改写
     *
     * @param oldSql
     * @param backDto
     * @param esResult
     * @return
     * @throws Exception
     */
    public static String rewriteBackSql(String oldSql, BackDto backDto, List<?> esResult) throws Exception {
        CCJSqlParserManager parserManager = new CCJSqlParserManager();
        Select select = (Select) parserManager.parse(new StringReader(oldSql));
        PlainSelect plain = (PlainSelect) select.getSelectBody();
        //where 添加回表条件
        setBackWhereItem(plain, backDto, esResult);
//        plain.setGroupByElement(null);
//        plain.setHaving(null);
//        plain.setOrderByElements(null);
        return select.toString();
    }

    /**
     * 回表sql where改写
     *
     * @param plain
     * @param backDto
     * @param esResult
     * @throws Exception
     */
    private static void setBackWhereItem(PlainSelect plain, BackDto backDto, List<?> esResult) throws Exception {
        String tableName = getBackTableName(plain, backDto);
        //ColumnName es驼峰 转 mysql下划线 
        String backColumn = backDto.getBackColumn();
        if (!EsEngineConfig.isNamingStrategy()) {
            backColumn = CaseFormatUtils.camelToUnderscore(backColumn);
        }
        String backSql = " " + tableName + "." + backColumn + " in (" + SqlParamParseHelper.getListParameterValue(esResult) + ")";
        if (StringUtils.isNotEmpty(backSql)) {
            if (plain.getWhere() == null) {
                plain.setWhere(CCJSqlParserUtil.parseCondExpression(backSql));
            } else {
                plain.setWhere(new AndExpression(plain.getWhere(), CCJSqlParserUtil.parseCondExpression(backSql)));
            }
        }
    }

    /**
     * 回去回表查询表名
     *
     * @param plain
     * @param backDto
     * @return
     */
    private static String getBackTableName(PlainSelect plain, BackDto backDto) {
        String tableName;
        if (StringUtils.isNotEmpty(backDto.getTableName())) {
            tableName = getJoinTableName(plain).get(backDto.getTableName());
        } else {
            tableName = getDefaultFromTableName(plain);
        }
        if (StringUtils.isEmpty(tableName)) {
            throw new EsEngineQueryException("回表查询指定的表名不存在: " + backDto.getTableName());
        }
        return tableName;
    }

    /**
     * 获取表名(关联查询时 默认表名为主表表名)
     *
     * @param plain
     * @return
     */
    private static String getDefaultFromTableName(PlainSelect plain) {
        FromItem fromItem = plain.getFromItem();
        String fromItemName = "";
        if (fromItem instanceof Table) {
            fromItemName = ((Table) fromItem).getName();
        }
        return fromItem.getAlias() == null ? fromItemName : fromItem.getAlias().getName();
    }

    /**
     * 获取表名(主表表名,及关联表表名, key为原始表名, value为原始表名或别名)
     *
     * @param plain
     * @return
     */
    private static Map<String, String> getJoinTableName(PlainSelect plain) {
        List<FromItem> fromItems = Lists.newArrayList();
        Map<String, String> tableNames = Maps.newHashMap();
        fromItems.add(plain.getFromItem());
        if (CollectionUtils.isNotEmpty(plain.getJoins())) {
            plain.getJoins().forEach(item -> fromItems.add(item.getRightItem()));
        }

        fromItems.forEach(fromItem -> {
            String fromItemName = "";
            if (fromItem instanceof Table) {
                fromItemName = ((Table) fromItem).getName();
            }
            tableNames.put(fromItemName, fromItem.getAlias() == null ? fromItemName : fromItem.getAlias().getName());
        });
        return tableNames;
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
            throw new EsEngineQueryException("undefine query-index @EsQueryIndex");
        }
        plain.setFromItem(new Table(ann.value()));
    }

    /**
     * 设置替换select 里面的字段
     *
     * @param plainSelect
     * @return
     */
    public static void setSelectItem(PlainSelect plainSelect, Boolean isCleanAs, Map<String, String> tableAlias) {
        for (SelectItem selectItem : plainSelect.getSelectItems()) {
            selectItem.accept(new SelectItemVisitorAdapter() {
                @Override
                public void visit(SelectExpressionItem item) {
                    if (item.getExpression() instanceof Function) {
                        setFunction((Function) item.getExpression(), tableAlias);
                    } else if (item.getExpression() instanceof CaseExpression) {
                        setCaseExpression((CaseExpression) item.getExpression(), tableAlias);
                    } else if (item.getExpression() instanceof Column) {
                        Column column = (Column) item.getExpression();
                        //清除t.
                        reNameColumnName(column, tableAlias);
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
     * 清除原本的item
     * 设置回表查item
     *
     * @param select
     * @param backDto
     */
    private static void setSelectItem(Select select, BackDto backDto) {
        PlainSelect plain = (PlainSelect) select.getSelectBody();
        List<SelectItem> selectItems = plain.getSelectItems();
        selectItems.clear();
        SelectUtils.addExpression(select, new Column(backDto.getBackColumn()));
    }

    /**
     * 设置替换where里面的字段
     *
     * @param
     * @return
     */
    public static void setWhereItem(Expression where, Map<String, String> tableAlias) {
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
            reNameColumnName(rightColumn, tableAlias);
            //清除表别名
            rightColumn.setTable(new Table());
        } else if (rightExpression instanceof Function) {
            setFunction((Function) rightExpression, tableAlias);
        } else {
            setWhereItem(rightExpression, tableAlias);
        }
        if (leftExpression instanceof Column) {
            Column leftColumn = (Column) leftExpression;
            reNameColumnName(leftColumn, tableAlias);
            //清除表别名
            leftColumn.setTable(new Table());
        } else if (leftExpression instanceof Function) {
            setFunction((Function) leftExpression, tableAlias);
        } else {
            setWhereItem(leftExpression, tableAlias);
        }
    }

    /**
     * 设置替换groupBy里面的字段
     *
     * @param groupBy
     */
    private static void setGroupItem(GroupByElement groupBy, Map<String, String> tableAlias) {
        if (groupBy == null) {
            return;
        }
        List<Expression> groupByExpressions = groupBy.getGroupByExpressions();
        groupByExpressions.forEach(item -> {
            if (item instanceof Column) {
                Column groupColumn = (Column) item;
                reNameColumnName(groupColumn, tableAlias);
                groupColumn.setTable(new Table());
            }
        });
    }

    /**
     * 设置替换having里面的字段
     *
     * @param having
     */
    private static void setHavingItem(Expression having, Map<String, String> tableAlias) {
        if (having == null) {
            return;
        }
        setWhereItem(having, tableAlias);
    }

    /**
     * 设置替换orderBy里面的字段
     *
     * @param orderByElements
     */
    private static void setOrderItem(List<OrderByElement> orderByElements, Map<String, String> tableAlias) {
        if (CollectionUtils.isEmpty(orderByElements)) {
            return;
        }
        orderByElements.forEach(item -> {
            Expression expression = item.getExpression();
            if (expression instanceof Column) {
                Column groupColumn = (Column) expression;
                reNameColumnName(groupColumn, tableAlias);
                groupColumn.setTable(new Table());
            }
        });
    }


    /**
     * 设置替换select 中 function里面的字段
     *
     * @param function
     */
    private static void setFunction(Function function, Map<String, String> tableAlias) {
        if (function.getParameters() == null || function.getParameters().getExpressions() == null) {
            return;
        }
        List<Expression> list = function.getParameters().getExpressions();
        list.forEach(data -> {
            if (data instanceof Function) {
                setFunction((Function) data, tableAlias);
            } else if (data instanceof Column) {
                Column column = (Column) data;
                reNameColumnName(column, tableAlias);
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
    public static void setCaseExpression(CaseExpression caseExpression, Map<String, String> tableAlias) {
        if (caseExpression.getWhenClauses() == null) {
            return;
        }
        List<WhenClause> list = caseExpression.getWhenClauses();
        list.forEach(data -> {
            if (data != null) {
                setWhereItem(data.getWhenExpression(), tableAlias);
            }
        });
    }

    /**
     * ColumnName mysql 下划线 转es驼峰
     *
     * @param column
     */
    private static void reNameColumnName(Column column, Map<String, String> tableAlias) {
        //替换mysql和es的别名映射
        if (!tableAlias.isEmpty()) {
            String columnName = column.toString();
            //判断是否包含两个'.' 替换掉jooq的库名(jooq生成的字段格式为 `user`.`person`.`person_no`)
            int n = columnName.length() - LocalStringUtils.replaceSpot(columnName).length();
            if (n > NumberUtils.INTEGER_ONE) {
                columnName = columnName.substring(columnName.indexOf(".") + 1);
            }
            if (tableAlias.containsKey(columnName)) {
                column.setColumnName(tableAlias.get(columnName));
            }
        }
        //清除t.
        if (!EsEngineConfig.isNamingStrategy()) {
            String columnName = column.getColumnName();
            columnName = CaseFormatUtils.underscoreToCamel(columnName);
            column.setColumnName(columnName);
        }

    }

}
