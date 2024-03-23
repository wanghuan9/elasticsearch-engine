package com.elasticsearch.engine.base.common.utils;


import com.elasticsearch.engine.base.common.parse.sql.SqlParamParseHelper;
import com.elasticsearch.engine.base.common.proxy.handler.exannotation.AnnotationQueryCommon;
import com.elasticsearch.engine.base.model.annotion.ESColumn;
import com.elasticsearch.engine.base.model.annotion.EsQueryIndex;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Predicate;

/**
 * @author wanghuan
 * @description ReflectionUtils
 * @mail 958721894@qq.com
 * @date 2022/6/9 14:11
 */
public class ReflectionUtils {

    public static boolean isBaseTypeAndExtend(Class<?> type) {
        return isBaseType(type) || isExtendsType(type);
    }

    /**
     * judge the given type is Java-Base type or String, but not void.class
     *
     * @param type return
     */
    public static boolean isBaseType(Class<?> type) {
        return (type.isPrimitive() && !Objects.equals(type, void.class))
                || type.equals(String.class) || type.equals(Boolean.class)
                || type.equals(Integer.class) || type.equals(Long.class) || type.equals(Short.class)
                || type.equals(Float.class) || type.equals(Double.class)
                || type.equals(Byte.class) || type.equals(Character.class);
    }

    /**
     * 判断args是否都为基本类型
     *
     * @param args
     * @return
     */
    public static boolean allParamIsBaseType(Object[] args) {
        if (args == null || args.length == 0) {
            return false;
        }
        for (Object obj : args) {
            if (!isBaseTypeAndExtend(obj.getClass()) && !(obj instanceof List)) {
                return false;
            }
        }
        return true;
    }

    /**
     * initialize target-class
     *
     * @param clazz target-class， must have no-args public constructor
     * @param <T>   target-class-type
     *              return
     */
    public static <T> T newInstance(Class<T> clazz) {
        try {
            Constructor<T> clazzConstructor = clazz.getConstructor();
            T targetBean = clazzConstructor.newInstance();
            return targetBean;
        } catch (NoSuchMethodException e) {
            throw new RuntimeException("target Class don't have No-args Constructor, cause: ", e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException("invoke targetConstructor Error, can't invoke , cause: ", e);
        } catch (InstantiationException e) {
            throw new RuntimeException("invoke targetConstructor Error, cause: ", e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException("constructor isn't public, can't access, cause: ", e);
        }
    }

    /**
     * invoke target method
     */
    public static Object methodInvoke(Object instance, Method method, Object... args) {
        try {
            return method.invoke(instance, args);
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Reflect-Invoke-TargetMethod IllegalAccessException Error,cause:", e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException("Reflect-Invoke-TargetMethod InvocationTargetException Error,cause:", e);
        }
    }

    /**
     * use reflect to set a value for a given field
     */
    public static void setFieldValue(Object instance, Field field, Object val, boolean nullable) {
        try {
            field.setAccessible(true);
            if (Objects.nonNull(val) || nullable) {
                field.set(instance, val);
            }
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Reflect-setValue IllegalAccessException Error,cause:", e);
        }
    }

    public static Object getFieldValue(Field field, Object target) {
        if (!field.isAccessible()) {
            field.setAccessible(true);
        }
        try {
            return field.get(target);
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Reflect-getValue IllegalAccessException Error, cause:", e);
        }
    }

    public static Collection transArrayOrCollection(Object targetObj) {
        Class<?> returnType = targetObj.getClass();
        //判断返回类型是否是集合类型
        boolean isCollection = Collection.class.isAssignableFrom(returnType);
        if (isCollection) {
            return (Collection) targetObj;
        }
        //判断返回类型是否是数组类型
        boolean isArray = returnType.isArray();
        if (isArray) {
            int length = Array.getLength(targetObj);
            List<Object> arr = Lists.newArrayList();
            for (int i = 0; i < length; i++) {
                arr.add(Array.get(targetObj, i));
            }
            return arr;
        }
        return Lists.newArrayList(targetObj);
    }

    /**
     * 获取这个类的所有父类
     *
     * @param clazz
     * @return
     */
    public static List<Class<?>> getSuperClass(Class<?> clazz) {
        List<Class<?>> clazzs = new ArrayList<Class<?>>();
        Class<?> suCl = clazz.getSuperclass();
        while (suCl != null) {
            System.out.println(suCl.getName());
            clazzs.add(suCl);
            suCl = suCl.getSuperclass();
        }
        return clazzs;
    }

    /**
     * 判断是否是List类型切List元素为基本类型
     *
     * @param param
     * @param val
     * @pubrn
     */
    public static boolean checkCollectionValueType(Parameter param, Object val) {
        Predicate<Parameter> checkCollectionTypePredicate = f -> {
            ParameterizedType genericType = (ParameterizedType) f.getParameterizedType();
            Type[] actualType = genericType.getActualTypeArguments();
            String fullClassPath = actualType[0].getTypeName();
            Class<?> clazz;
            try {
                clazz = Class.forName(fullClassPath);
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
            return actualType.length == 1 && ReflectionUtils.isBaseType(clazz);
        };
        return (val instanceof List) && checkCollectionTypePredicate.test(param);
    }

    /**
     * 基本参数支持的扩展类型
     *
     * @param type
     * @return
     */
    public static boolean isExtendsType(Class<?> type) {
        return type.equals(LocalDateTime.class) || type.equals(LocalDate.class) || type.equals(BigDecimal.class);

    }


    /**
     * 获取对象的filed name 和 value,支持嵌套
     * file 的name 格式为 为 objectName.filedName的形式
     *
     * @param view
     * @param paramName
     * @return
     * @throws IllegalAccessException
     */
    public static Map<String, Object> getNestedFieldsMap(String paramName, Object view) {
        Map<String, Object> map = new HashMap<>(26);
        try {
            getFields(view, map, paramName);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        return map;
    }

    /**
     * 递归获取对象的 filed name 和 value
     *
     * @param view
     * @param map
     * @param parentName
     * @throws IllegalAccessException
     */
    public static void getFields(Object view, Map<String, Object> map, String parentName) throws IllegalAccessException {
        Class<?> clazz = view.getClass();
        Field[] fieldArr = clazz.getDeclaredFields();
        for (Field field : fieldArr) {
            field.setAccessible(true);
            String name = parentName + "." + field.getName();
            Object val = field.get(view);
            if (isBaseTypeAndExtend(field.getType())) {
                String parameterVal = SqlParamParseHelper.getParameterValue(val);
                map.put(name, parameterVal);
            } else {
                getFields(val, map, name);
            }
        }
    }

    /**
     * 获取mybatis映射es的字段别名
     *
     * @param method
     * @return
     */
    public static Map<String, String> getEsAlias(Method method, Map<String, String> tableNames) {
        //方法返回值
        Class<?> queryIndex = null;
        Class<?> returnType = method.getReturnType();
        if (Objects.nonNull(returnType.getAnnotation(EsQueryIndex.class))) {
            queryIndex = returnType;
        }
        //方法返回值的泛型
        Class<?> returnGenericType = AnnotationQueryCommon.getReturnGenericType(method);
        if (Objects.isNull(queryIndex) && Objects.nonNull(returnGenericType) && Objects.nonNull(returnGenericType.getAnnotation(EsQueryIndex.class))) {
            queryIndex = returnGenericType;
        }

        Map<String, String> table = Maps.newHashMap();
        if (Objects.nonNull(queryIndex)) {
            Field[] fieldArr = queryIndex.getDeclaredFields();
            for (Field field : fieldArr) {
                field.setAccessible(true);
                ESColumn esColumn = field.getAnnotation(ESColumn.class);
                if (Objects.nonNull(esColumn)) {
                    table.put(getSqlColumn(esColumn, tableNames), esColumn.esColumn());
                }
            }
        }
        return table;
    }

    /**
     * getSqlColumn
     *
     * @param esColumn
     * @param tableNames
     * @return
     */
    private static String getSqlColumn(ESColumn esColumn, Map<String, String> tableNames) {
        String tableName = tableNames.get(esColumn.table());
        if (StringUtils.isNotEmpty(tableName)) {
            return tableName + "." + esColumn.sqlColumn();
        }
        return esColumn.sqlColumn();
    }

}
