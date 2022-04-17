package com.elasticsearch.engine.elasticsearchengine.common.proxy.handler.impl;

import com.elasticsearch.engine.elasticsearchengine.common.proxy.enums.EsAnnotationQueryEnum;
import com.elasticsearch.engine.elasticsearchengine.common.proxy.enums.EsQueryProxyExecuteEnum;
import com.elasticsearch.engine.elasticsearchengine.common.proxy.handler.EsQueryProxyExecuteHandler;
import com.elasticsearch.engine.elasticsearchengine.common.proxy.handler.exannotation.EsAnnotationQueryFactory;
import com.elasticsearch.engine.elasticsearchengine.common.utils.ReflectionUtils;
import com.elasticsearch.engine.elasticsearchengine.common.utils.ThreadLocalUtil;
import com.elasticsearch.engine.elasticsearchengine.model.annotion.EsQueryIndex;
import com.elasticsearch.engine.elasticsearchengine.model.constant.CommonConstant;
import com.elasticsearch.engine.elasticsearchengine.model.exception.EsHelperQueryException;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.lang.reflect.Method;

/**
 * @author wanghuan
 * @description: es注解查询代理实现逻辑
 * @date 2022-04-15 18:01
 */
@Component
public class EsAnnotationQuery implements EsQueryProxyExecuteHandler {

    @Resource
    private EsAnnotationQueryFactory esAnnotationQueryFactory;

    @Override
    public Boolean matching(EsQueryProxyExecuteEnum factory) {
        return EsQueryProxyExecuteEnum.ANNOTATION_QUERY.equals(factory);
    }

    //代理类的泛型, 或者自定义泛型获取的公共方法
    @Override
    public Object handle(Object proxy, Method method, Object[] args) {
        String prefix = ThreadLocalUtil.get(CommonConstant.INTERFACE_METHOD_NAME);
        if (args == null || args.length == 0) {
            throw new EsHelperQueryException(prefix + "missing parameters");
        }
        EsAnnotationQueryEnum queryEnum;
        Class<?> clazz = args[0].getClass();
        //只有一个参数 && 并且参数不是基础类型 && 有@EsQueryIndex注解
        if (args.length == NumberUtils.INTEGER_ONE && !ReflectionUtils.isBaseType(clazz) && clazz.isAnnotationPresent(EsQueryIndex.class)) {
            queryEnum = EsAnnotationQueryEnum.ANNOTATION_MODEL_QUERY;
        } else if (args.length > NumberUtils.INTEGER_ZERO && ReflectionUtils.allParamIsBaseType(args)) {
            //有一个或多个参数 && 都是基础类型
            queryEnum = EsAnnotationQueryEnum.ANNOTATION_PARAM_QUERY;
        } else {
            throw new EsHelperQueryException(prefix + "方法参数异常: 查询参数不被支持,仅支持多个基本类型参数 或 者单个引用类型的参数并标记@EsQueryIndex注解");
        }
        return esAnnotationQueryFactory.getBean(queryEnum).handle(proxy, method, args);
    }


}
