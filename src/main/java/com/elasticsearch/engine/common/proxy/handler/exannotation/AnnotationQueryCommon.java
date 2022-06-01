package com.elasticsearch.engine.common.proxy.handler.exannotation;

import com.elasticsearch.engine.model.domain.BaseResp;
import com.elasticsearch.engine.model.exception.EsHelperExecuteException;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author wanghuan
 * @description: 注解proxy查询handler工具类
 * @date 2022-04-16 23:08
 */
public class AnnotationQueryCommon {

    /**
     * 获取clazz 实现的 implClass接口对应的第一个泛型的class
     *
     * @param clazz
     * @return
     */
    public static Class<?> getClazzImplClassGeneric(Class<?> clazz, Class<?> implClass) {
        Type type = getClazzImplClassGenericType(clazz, implClass);
        //获取到Repository泛型的Entity类
        if (type instanceof Class) {
            return (Class<?>) type;
        }
        return null;
    }

    /**
     * 获取方法返回值对应的泛型
     *
     * @param method
     * @return
     */
    public static Class<?> getReturnGenericType(Method method) {
        Class<?> actualType = null;
        Class<?> returnType = method.getReturnType();
        if (returnType == List.class || returnType == BaseResp.class) {
            Type type = method.getGenericReturnType();
            if (type instanceof ParameterizedType) {
                Type[] actualTypeArguments = ((ParameterizedType) type).getActualTypeArguments();
                //因为list泛型只有一个值 所以直接取0下标
                String typeName = actualTypeArguments[0].getTypeName();
                //真实返回值类型 Class对象
                try {
                    actualType = Class.forName(typeName);
                } catch (ClassNotFoundException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        return actualType;
    }

    /**
     * 获取clazz 实现的 implClass接口对应的第一个泛型的class
     *
     * @param clazz
     * @return
     */
    public static Type getClazzImplClassGenericType(Class<?> clazz, Class<?> implClass) {
        EsHelperExecuteException esHelperExecuteException = new EsHelperExecuteException("泛型声明异常: " + clazz.getSimpleName() + " implements " + implClass.getSimpleName());
        //获取class实现的接口
        Type[] interfaces = clazz.getGenericInterfaces();
        Map<? extends Class<?>, ParameterizedType> collect = Arrays.stream(interfaces).map(type -> {
            if (!(type instanceof ParameterizedType)) {
                throw new EsHelperExecuteException("泛型声明异常: " + clazz.getSimpleName() + " 缺少泛型声明");
            }
            return (ParameterizedType) type;
        }).collect(Collectors.toMap(item -> {
            try {
                return Class.forName(item.getRawType().getTypeName());
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }, Function.identity()));

        ParameterizedType parameterizedType = collect.get(implClass);
        if (Objects.isNull(parameterizedType)) {
            throw esHelperExecuteException;
        }

        Type[] actualTypeArguments = parameterizedType.getActualTypeArguments();
        if (actualTypeArguments == null || actualTypeArguments.length < 1) {
            throw esHelperExecuteException;
        }
        //获取到Repository泛型的Entity类
        return actualTypeArguments[0];
    }

}
