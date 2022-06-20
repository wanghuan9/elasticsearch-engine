package com.elasticsearch.engine.common.parse.sql;

import com.elasticsearch.engine.common.utils.DateUtils;
import com.elasticsearch.engine.common.utils.ReflectionUtils;
import com.elasticsearch.engine.model.emenu.SqlParamParse;
import com.elasticsearch.engine.model.exception.EsEngineQueryException;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.reflection.MetaObject;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.type.TypeHandlerRegistry;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.text.DateFormat;
import java.time.LocalDateTime;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author wanghuan
 * @description: ROOD
 * @date 2022-05-10 21:37
 */
public class SqlParamParseHelper {

    /**
     * mybatis查询参数解析 sql中的？进行参数替换
     *
     * @param configuration
     * @param boundSql
     * @return
     */
    public static String paramParse(Configuration configuration, BoundSql boundSql) {
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
                sql = sql.replaceAll("\\?", Matcher.quoteReplacement(getParameterValue(parameterObject)));
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
        //like concat处理
        return parseLikeConcat(sql);
    }


    /**
     * 自定义注解查询参数解析 替换sql中的占位符
     *
     * @param sql
     * @param method
     * @param args
     * @param sqlParamParse
     * @return
     */
    public static String getMethodArgsSqlAnn(String sql, Method method, Object[] args, SqlParamParse sqlParamParse) {
        //参数为空直接返回无需解析
        if (Objects.isNull(args) || args.length < 1) {
            return sql;
        }
        Map<String, Object> paramMap = getParamMapAnn(method, args, sqlParamParse);
        //替换参数
        String sqlParam = renderStringAnn(sql, paramMap, sqlParamParse);
        //like concat处理
        return parseLikeConcat(sqlParam);
    }

    /**
     * getMethodArgsSqlAnn
     * 获取方法的所有参数
     *
     * @param method
     * @param args
     * @return
     */
    public static Map<String, Object> getParamMapAnn(Method method, Object[] args, SqlParamParse sqlParamParse) {
        Map<String, Object> map = new HashMap<>();
        Parameter[] parameters = method.getParameters();
        for (int i = 0; i < parameters.length; i++) {
            Parameter parameter = parameters[i];
            Object val = args[i];
            boolean isBase = ReflectionUtils.isBaseTypeAndExtend(parameter.getType());
            String parameterName = parameter.getName();
            if (isBase) {
                //基础类型解析
                map.put(parameterName, getParameterValue(val));
            } else if (List.class.isAssignableFrom(parameter.getType()) && ReflectionUtils.checkCollectionValueType(parameter, val)) {
                //List类型解析
                if (val instanceof List) {
                    map.put(parameterName, getListParameterValue(val));
                }
            } else {
                //对象Object解析
                map.putAll(ReflectionUtils.getNestedFieldsMap(parameter.getName(), val));
            }
        }
        return map;
    }


    /**
     * getMethodArgsSqlAnn
     * 拼接sql的参数
     *
     * @param sql
     * @param map
     * @return
     */
    public static String renderStringAnn(String sql, Map<String, Object> map, SqlParamParse sqlParamParse) {
        for (Map.Entry<String, Object> e : map.entrySet()) {
            String format = String.format(sqlParamParse.getFormatStr(), e.getKey());
            if (sql.contains(format)) {
                String regex = SqlParamParse.getRegex(String.format(sqlParamParse.getRegexStr(), e.getKey()));
                Pattern pattern = Pattern.compile(regex);
                Matcher matcher = pattern.matcher(sql);
                sql = matcher.replaceAll(e.getValue().toString());
            }

            String likeFormat = String.format(sqlParamParse.getLikeformatStr(), e.getKey());
            if (sql.contains(likeFormat)) {
                String likeRegex = SqlParamParse.getRegex(String.format(sqlParamParse.getLikeRegexStr(), e.getKey()));
                Pattern likePattern = Pattern.compile(likeRegex);
                Matcher likeMatcher = likePattern.matcher(sql);
                sql = likeMatcher.replaceAll(e.getValue().toString().replaceAll("'", ""));
            }
        }
        //参数替换完之后如果还包含"#{" 说明有参数没有被替换
        if (sql.contains(sqlParamParse.getPlaceHolder())) {
            throw new EsEngineQueryException("方法中的参数和sql中的参数 不匹配");
        }
        return sql;
    }


    /**
     * 替换sql中的占位符
     *
     * @param sql
     * @param method
     * @param args
     * @param sqlParamParse
     * @return
     */
    public static String getMethodArgsSqlJpa(String sql, Method method, Object[] args, SqlParamParse
            sqlParamParse) {
        //参数为空直接返回无需解析
        if (Objects.isNull(args) || args.length < 1) {
            return sql;
        }
        List<Object> param = getParamMapJpa(method, args);
        //替换参数
        String sqlParam = renderStringJpa(sql, param, sqlParamParse);
        //like concat处理
        return parseLikeConcat(sqlParam);
    }


    /**
     * Jpa查询参数解析 替换sql中的占位符
     *
     * @param method
     * @param args
     * @return
     */
    private static List<Object> getParamMapJpa(Method method, Object[] args) {
        List<Object> list = new ArrayList<>();
        Parameter[] parameters = method.getParameters();
        for (int i = 0; i < parameters.length; i++) {
            Parameter parameter = parameters[i];
            Object val = args[i];
            if (List.class.isAssignableFrom(parameter.getType()) && ReflectionUtils.checkCollectionValueType(parameter, val)) {
                if (val instanceof List) {
                    List listParam = (List) val;
                    if (!listParam.isEmpty()) {
                        listParam.forEach(item -> list.add(getParameterValue(item)));
                    }

                }
            } else {
                list.add(getParameterValue(val));
            }
        }
        return list;
    }

    /**
     * jpa参数替换
     *
     * @param sql
     * @param param
     * @param sqlParamParse
     * @return
     */
    private static String renderStringJpa(String sql, List<Object> param, SqlParamParse sqlParamParse) {
        //无需填充参数的情况
        if (!sql.contains(sqlParamParse.getPlaceHolder())) {
            return sql;
        }
        for (Object val : param) {
            String regex = String.format(sqlParamParse.getRegexStr(), val);
            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(sql);
            sql = matcher.replaceFirst(val.toString());
        }
        //参数替换完之后如果还包含"#{" 说明有参数没有被替换
        if (sql.contains(sqlParamParse.getPlaceHolder())) {
            throw new EsEngineQueryException("方法中的参数和sql中的参数 不匹配");
        }
        return sql;
    }

    /**
     * list 参数解析
     *
     * @param val
     * @return
     */
    public static String getListParameterValue(Object val) {
        List listParam = (List) val;
        StringBuffer sb = new StringBuffer();
        if (!listParam.isEmpty()) {
            listParam.forEach(item -> sb.append(getParameterValue(item)).append(","));
        }
        String param = sb.toString();
        return param.substring(0, param.length() - 1);
    }

    /**
     * 参数解析
     * 如果参数是String，则添加单引号， 如果是日期，则转换为时间格式器并加单引号； 对参数是null和不是null的情况作了处理
     *
     * @param obj
     * @return
     */
    public static String getParameterValue(Object obj) {
        if (obj == null) {
            return "null";
        }
        String value;
        if (obj instanceof String) {
            value = "'" + obj + "'";
        } else if (obj instanceof LocalDateTime) {
            String formatVal = DateUtils.formatDefault((LocalDateTime) obj);
            value = "'" + formatVal + "'";
        } else if (obj instanceof Date) {
            DateFormat formatter = DateFormat.getDateTimeInstance(DateFormat.DEFAULT, DateFormat.DEFAULT, Locale.CHINA);
            value = "'" + formatter.format(new Date()) + "'";
        } else {
            value = obj.toString();
        }
        return value;
    }

    /**
     * 解析Like Concat函数
     *
     * @param sql
     * @return
     */
    public static String parseLikeConcat(String sql) {
        int concatIndex = sql.indexOf("CONCAT");
        if (concatIndex == -1) {
            concatIndex = sql.indexOf("concat");
        }
        if (concatIndex > 0) {
            return parseLikeConcat(doParseLikeConcat(sql, concatIndex));
        }
        return sql;
    }

    /**
     * 执行解析Like Concat函数
     *
     * @param sql
     * @param indexConcat
     * @return
     */
    public static String doParseLikeConcat(String sql, Integer indexConcat) {
        //concat之后的串
        String concatAfter = sql.substring(indexConcat);
        //concat后第一个'('
        int i = concatAfter.indexOf("(");
        //concat后第一个')'
        int j = concatAfter.indexOf(")");
        String likeBody = concatAfter.substring(i + 1, j);
        String[] split = likeBody.split(",");
        StringBuilder likeSb = new StringBuilder();
        for (String s : split) {
            likeSb.append(s.trim().replaceAll("'", ""));
        }
        //替换后的like参数
        String likeParam = "'" + likeSb + "'";
        //重新拼接concat前半段+替换后的like+concat后半段
        return sql.substring(0, indexConcat).concat(likeParam) + sql.substring(j + indexConcat + 1);
    }

}
