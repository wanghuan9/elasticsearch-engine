package com.elasticsearch.engine.elasticsearchengine.common.utils;


import com.google.common.collect.Lists;

import java.lang.reflect.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

/**
 * project  elasticsearch-helper
 * packages   org.pippi.elasticsearch.helper.core.utils
 * date     2021/7/18
 * author    JohenTeng
 * email    1078481395@qq.com
 **/
public class ReflectionUtils {

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

}
