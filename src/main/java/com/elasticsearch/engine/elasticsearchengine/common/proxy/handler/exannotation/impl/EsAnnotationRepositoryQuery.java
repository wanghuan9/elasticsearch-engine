package com.elasticsearch.engine.elasticsearchengine.common.proxy.handler.exannotation.impl;

import com.elasticsearch.engine.elasticsearchengine.common.proxy.enums.EsAnnotationQueryEnum;
import com.elasticsearch.engine.elasticsearchengine.common.proxy.handler.exannotation.EsAnnotationQueryHandler;
import com.elasticsearch.engine.elasticsearchengine.common.queryhandler.EsExecuteHandler;
import com.elasticsearch.engine.elasticsearchengine.hook.ResponseHook;
import com.elasticsearch.engine.elasticsearchengine.model.domain.BaseESRepository;
import com.elasticsearch.engine.elasticsearchengine.model.domain.BaseResp;
import com.elasticsearch.engine.elasticsearchengine.model.exception.EsHelperExecuteException;
import com.elasticsearch.engine.elasticsearchengine.model.exception.EsHelperQueryException;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
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
 * @description: model注解proxy查询handler
 * @date 2022-04-15 19:08
 */
@Component
public class EsAnnotationRepositoryQuery implements EsAnnotationQueryHandler {

    @Resource
    private EsExecuteHandler esExecuteHandler;


    @Override
    public Boolean matching(EsAnnotationQueryEnum factory) {
        return EsAnnotationQueryEnum.ANNOTATION_MODEL_QUERY.equals(factory);
    }

    @Override
    public Object handle(Object proxy, Method method, Object[] args) {
        if (args == null || args.length == 0 || args.length > 1) {
            throw new EsHelperQueryException("ES-HELPER un-support multi-params or miss-param, params must be single");
        }
        //方法参数
        Object param = args[0];
        //方法返回值
        Class<?> returnType = method.getReturnType();
        Type returnTypeType = method.getGenericReturnType();
        //方法返回值的泛型
        Class<?> returnGenericType = getReturnGenericType(method);
        //获取到Repository泛型的Entity类
        Class<?> retEntityClass = getClazzImplClassGeneric(method.getDeclaringClass(), BaseESRepository.class);
        //获取到param参数 实现的ResponseHook中的泛型类
        Type responseHookResultType = null;
        if (ResponseHook.class.isAssignableFrom(param.getClass())) {
            responseHookResultType = getClazzImplClassGenericType(param.getClass(), ResponseHook.class);
        }
        //Repository泛型 单个返回值
        if (returnType.isAssignableFrom(retEntityClass)) {
            return esExecuteHandler.executeOne(param, returnType);
        }
        //Repository泛型 List返回值
        if (List.class.isAssignableFrom(returnType)
                && Objects.nonNull(returnGenericType)
                && returnGenericType.isAssignableFrom(retEntityClass)) {
            return esExecuteHandler.executeList(param, returnGenericType);
        }
        //Repository泛型 BaseResp返回值
        if (BaseResp.class.isAssignableFrom(returnType)
                && Objects.nonNull(returnGenericType)
                && returnGenericType.isAssignableFrom(retEntityClass)) {
            return esExecuteHandler.execute(param, returnGenericType);
        }
        //自定义ResponseHook返回值
        if (Objects.nonNull(responseHookResultType) && returnTypeType.getTypeName().equals(responseHookResultType.getTypeName())) {
            return esExecuteHandler.execute(param, returnType).getResult();
        }
        throw new EsHelperExecuteException("方法返回值泛型匹配异常: 返回值必须是 Repository 的泛型类型或 ResponseHook 的泛型类型");
    }

    /**
     * 获取clazz 实现的 implClass接口对应的第一个泛型的class
     *
     * @param clazz
     * @return
     */
    private Class<?> getClazzImplClassGeneric(Class<?> clazz, Class<?> implClass) {
        Type type = getClazzImplClassGenericType(clazz, implClass);
        //获取到Repository泛型的Entity类
        if (type instanceof Class) {
            return (Class<?>) type;
        }
        return null;
    }

    /**
     * 获取clazz 实现的 implClass接口对应的第一个泛型的class
     *
     * @param clazz
     * @return
     */
    private Type getClazzImplClassGenericType(Class<?> clazz, Class<?> implClass) {
        EsHelperExecuteException esHelperExecuteException = new EsHelperExecuteException("泛型声明异常: " + clazz.getSimpleName() + " implements " + implClass.getSimpleName());
        Type[] interfaces = clazz.getGenericInterfaces();
        if (interfaces == null && interfaces.length < 1) {
            throw esHelperExecuteException;
        }
        Map<? extends Class<?>, ParameterizedType> collect = Arrays.asList(interfaces).stream().map(type -> {
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
        if (actualTypeArguments == null && actualTypeArguments.length < 1) {
            throw esHelperExecuteException;
        }
        //获取到Repository泛型的Entity类
        return actualTypeArguments[0];
    }

    /**
     * 获取方法返回值对应的泛型
     *
     * @param method
     * @return
     */
    private Class<?> getReturnGenericType(Method method) {
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
}
