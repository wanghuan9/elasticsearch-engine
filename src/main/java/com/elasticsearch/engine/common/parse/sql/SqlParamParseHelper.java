package com.elasticsearch.engine.common.parse.sql;

import com.elasticsearch.engine.common.utils.DateUtils;
import com.elasticsearch.engine.common.utils.ReflectionUtils;
import com.elasticsearch.engine.model.emenu.SqlParamParse;
import com.elasticsearch.engine.model.exception.EsHelperQueryException;
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
     * 替换sql中的占位符
     *
     * @param sql
     * @param method
     * @param args
     * @param sqlParamParse
     * @return
     */
    public static String getMethodArgsSql(String sql, Method method, Object[] args, SqlParamParse sqlParamParse) {
        //参数为空直接返回无需解析
        if (Objects.isNull(args) || args.length<1){
            return sql;
        }
        Map<String, Object> paramMap = getParamMap(method, args);
        return renderString(sql, paramMap, sqlParamParse.getRegexStr(), sqlParamParse.getPlaceHolder());
    }

    /**
     * 拼接sql的参数
     *
     * @param sql
     * @param map
     * @return
     */
    public static String renderString(String sql, Map<String, Object> map, String regexStr, String placeHolder) {
        Set<Map.Entry<String, Object>> entries = map.entrySet();
        for (Map.Entry<String, Object> e : map.entrySet()) {
            String regex = String.format(regexStr, e.getKey());
            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(sql);
            sql = matcher.replaceAll(e.getValue().toString());
        }
        //参数替换完之后如果还包含"#{" 说明有参数没有被替换
        if (sql.contains(placeHolder)) {
            throw new EsHelperQueryException("方法中的参数和sql中的参数 不匹配");
        }
        return sql;
    }


    /**
     * 对mybatis sql中的？进行参数替换
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
        return sql;
    }


    /**
     * 获取方法的所有参数
     *
     * @param method
     * @param args
     * @return
     */
    public static Map<String, Object> getParamMap(Method method, Object[] args) {
        Map<String, Object> map = new HashMap<>();
        Parameter[] parameters = method.getParameters();
        for (int i = 0; i < parameters.length; i++) {
            Parameter parameter = parameters[i];
            Object val = args[i];
            if (parameter.getType().isAssignableFrom(List.class) && ReflectionUtils.checkCollectionValueType(parameter, val)) {
                if (val instanceof List) {
                    List listParam = (List) val;
                    StringBuffer sb = new StringBuffer();
                    if (!listParam.isEmpty()) {
                        listParam.forEach(item -> {
                            sb.append("'" + item + "'");
                        });
                    }
                    map.put(parameter.getName(), sb.toString());
                }
            } else if (val instanceof LocalDateTime) {
                String formatVal = DateUtils.formatDefault((LocalDateTime) val);
                String arg = "'" + formatVal + "'";
                map.put(parameter.getName(), arg);
            } else {
                String arg = "'" + val + "'";
                map.put(parameter.getName(), arg);
            }
        }
        return map;
    }

    /**
     * 如果参数是String，则添加单引号， 如果是日期，则转换为时间格式器并加单引号； 对参数是null和不是null的情况作了处理
     *
     * @param obj
     * @return
     */
    private static String getParameterValue(Object obj) {
        String value;
        if (obj instanceof String) {
            value = "'" + obj + "'";
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


}
