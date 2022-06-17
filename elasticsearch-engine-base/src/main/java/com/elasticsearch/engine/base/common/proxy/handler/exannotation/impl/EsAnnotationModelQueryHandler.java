package com.elasticsearch.engine.base.common.proxy.handler.exannotation.impl;

import com.elasticsearch.engine.base.common.proxy.enums.EsAnnotationQueryEnum;
import com.elasticsearch.engine.base.common.proxy.handler.exannotation.AnnotationQueryCommon;
import com.elasticsearch.engine.base.common.proxy.handler.exannotation.EsAnnotationQueryHandler;
import com.elasticsearch.engine.base.common.queryhandler.ann.model.EsExecuteHandler;
import com.elasticsearch.engine.base.common.utils.ThreadLocalUtil;
import com.elasticsearch.engine.base.hook.ResponseHook;
import com.elasticsearch.engine.base.model.constant.CommonConstant;
import com.elasticsearch.engine.base.model.domain.BaseESRepository;
import com.elasticsearch.engine.base.model.domain.BaseResp;
import com.elasticsearch.engine.base.model.domain.DefaultResp;
import com.elasticsearch.engine.base.model.exception.EsEngineExecuteException;
import com.elasticsearch.engine.base.model.exception.EsEngineQueryException;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Objects;

/**
 * @author wanghuan
 * @description: model注解proxy查询handler
 * @date 2022-04-15 19:08
 */
@Component
public class EsAnnotationModelQueryHandler implements EsAnnotationQueryHandler {

    @Resource
    private EsExecuteHandler esExecuteHandler;


    @Override
    public Boolean matching(EsAnnotationQueryEnum factory) {
        return EsAnnotationQueryEnum.ANNOTATION_MODEL_QUERY.equals(factory);
    }

    @Override
    public Object handle(Object proxy, Method method, Object[] args) {
        String prefix = ThreadLocalUtil.get(CommonConstant.INTERFACE_METHOD_NAME);
        if (args.length > 1) {
            throw new EsEngineQueryException(prefix + "ES-ENGINE un-support multi-params, params must be single");
        }
        //方法参数
        Object param = args[0];
        //方法返回值
        Class<?> returnType = method.getReturnType();
        Type returnTypeType = method.getGenericReturnType();
        //方法返回值的泛型
        Class<?> returnGenericType = AnnotationQueryCommon.getReturnGenericType(method);
        //获取到Repository泛型的Entity类
        Class<?> retEntityClass = AnnotationQueryCommon.getClazzImplClassGeneric(method.getDeclaringClass(), BaseESRepository.class);
        //获取到param参数 实现的ResponseHook中的泛型类
        Type responseHookResultType = null;
        if (ResponseHook.class.isAssignableFrom(param.getClass())) {
            responseHookResultType = AnnotationQueryCommon.getClazzImplClassGenericType(param.getClass(), ResponseHook.class);
        }
        //Repository泛型 单个返回值
        if (returnType.isAssignableFrom(retEntityClass)) {
            return esExecuteHandler.executeOne(method, param, returnType);
        }
        //Repository泛型 List返回值
        if (List.class.isAssignableFrom(returnType)
                && Objects.nonNull(returnGenericType)
                && returnGenericType.isAssignableFrom(retEntityClass)) {
            return esExecuteHandler.executeList(method, param, returnGenericType);
        }
        //Repository泛型 BaseResp返回值
        if (BaseResp.class.isAssignableFrom(returnType)
                && Objects.nonNull(returnGenericType)
                && returnGenericType.isAssignableFrom(retEntityClass)) {
            return esExecuteHandler.execute(method, param, returnGenericType);
        }
        //默认固定的实体响应
        if (BaseResp.class.isAssignableFrom(returnType)
                && Objects.nonNull(returnGenericType)
                && DefaultResp.class.isAssignableFrom(returnGenericType)) {
            return esExecuteHandler.executeDefaultResp(method, param);
        }
        //自定义ResponseHook返回值
        if (Objects.nonNull(responseHookResultType) && returnTypeType.getTypeName().equals(responseHookResultType.getTypeName())) {
            return esExecuteHandler.execute(method, param, returnType).getResult();
        }
        throw new EsEngineExecuteException(prefix + "方法返回值泛型匹配异常: 返回值必须是 Repository 的泛型类型或 ResponseHook 的泛型类型或 DefaultResp 的实现类");
    }

}
