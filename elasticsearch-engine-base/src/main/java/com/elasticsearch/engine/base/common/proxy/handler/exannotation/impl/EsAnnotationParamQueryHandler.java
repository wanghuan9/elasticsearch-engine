package com.elasticsearch.engine.base.common.proxy.handler.exannotation.impl;

import com.elasticsearch.engine.base.common.proxy.enums.EsAnnotationQueryEnum;
import com.elasticsearch.engine.base.common.proxy.handler.exannotation.AnnotationQueryCommon;
import com.elasticsearch.engine.base.common.proxy.handler.exannotation.EsAnnotationQueryHandler;
import com.elasticsearch.engine.base.common.queryhandler.ann.param.EsParamExecuteHandler;
import com.elasticsearch.engine.base.common.utils.ThreadLocalUtil;
import com.elasticsearch.engine.base.model.constant.CommonConstant;
import com.elasticsearch.engine.base.model.domain.BaseEsRepository;
import com.elasticsearch.engine.base.model.domain.BaseResp;
import com.elasticsearch.engine.base.model.exception.EsEngineExecuteException;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Objects;

/**
 * @author wanghuan
 * @description: param 注解proxy查询handler
 * @date 2022-04-16 22:08
 */
@Component
public class EsAnnotationParamQueryHandler implements EsAnnotationQueryHandler {

    @Resource
    private EsParamExecuteHandler esParamExecuteHandler;

    @Override
    public Boolean matching(EsAnnotationQueryEnum factory) {
        return EsAnnotationQueryEnum.ANNOTATION_PARAM_QUERY.equals(factory);
    }

    @Override
    public Object handle(Object proxy, Method method, Object[] args) {
        String prefix = ThreadLocalUtil.get(CommonConstant.INTERFACE_METHOD_NAME);
        //方法返回值
        Class<?> returnType = method.getReturnType();
        //方法返回值的泛型
        Class<?> returnGenericType = AnnotationQueryCommon.getReturnGenericType(method);
        //获取到Repository泛型的Entity类
        Class<?> retEntityClass = AnnotationQueryCommon.getClazzImplClassGeneric(method.getDeclaringClass(), BaseEsRepository.class);
        //Repository泛型 单个返回值
        if (Objects.isNull(retEntityClass)) {
            throw new EsEngineExecuteException(prefix + "泛型声明异常:Repository 缺少泛型声明");
        }
        if (returnType.isAssignableFrom(retEntityClass)) {
            return esParamExecuteHandler.executeOne(method, args, returnType);
        }
        //Repository泛型 List返回值
        if (List.class.isAssignableFrom(returnType)
                && Objects.nonNull(returnGenericType)
                && returnGenericType.isAssignableFrom(retEntityClass)) {
            return esParamExecuteHandler.executeList(method, args, returnGenericType);
        }

        //Repository泛型 BaseResp返回值
        if (BaseResp.class.isAssignableFrom(returnType)
                && Objects.nonNull(returnGenericType)
                && returnGenericType.isAssignableFrom(retEntityClass)) {
            return esParamExecuteHandler.execute(method, args, returnGenericType);
        }
        throw new EsEngineExecuteException(prefix + "方法返回值泛型匹配异常: 返回值必须是 Repository 的泛型类型");
    }

}
