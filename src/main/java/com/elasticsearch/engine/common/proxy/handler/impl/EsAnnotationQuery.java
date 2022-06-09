package com.elasticsearch.engine.common.proxy.handler.impl;

import com.elasticsearch.engine.common.proxy.enums.EsAnnotationQueryEnum;
import com.elasticsearch.engine.common.proxy.enums.EsQueryType;
import com.elasticsearch.engine.common.proxy.handler.EsQueryProxyExecuteHandler;
import com.elasticsearch.engine.common.proxy.handler.exannotation.EsAnnotationQueryFactory;
import com.elasticsearch.engine.common.utils.ReflectionUtils;
import com.elasticsearch.engine.common.utils.ThreadLocalUtil;
import com.elasticsearch.engine.model.constant.CommonConstant;
import com.elasticsearch.engine.model.exception.EsEngineQueryException;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.lang.reflect.Method;
import java.util.List;

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
    public Boolean matching(EsQueryType factory) {
        return EsQueryType.ANNOTATION.equals(factory);
    }

    //代理类的泛型, 或者自定义泛型获取的公共方法
    @Override
    public Object handle(Object proxy, Method method, Object[] args) {
        String prefix = ThreadLocalUtil.get(CommonConstant.INTERFACE_METHOD_NAME);
        if (args == null || args.length == 0) {
            throw new EsEngineQueryException(prefix + "missing parameters");
        }
        EsAnnotationQueryEnum queryEnum;
        Class<?> clazz = args[0].getClass();
        //只有一个参数 && 并且参数不是基础类型 && 并且参数不是List
        if (args.length == NumberUtils.INTEGER_ONE && !ReflectionUtils.isBaseTypeAndExtend(clazz) && (clazz.isAssignableFrom(List.class))) {
            queryEnum = EsAnnotationQueryEnum.ANNOTATION_MODEL_QUERY;
        } else if (args.length > NumberUtils.INTEGER_ZERO && ReflectionUtils.allParamIsBaseType(args)) {
            //有一个或多个参数 && 都是基础类型(包括List,LocalDateTime,LocalDate,BigDecimal)
            queryEnum = EsAnnotationQueryEnum.ANNOTATION_PARAM_QUERY;
        } else {
            throw new EsEngineQueryException(prefix + "方法参数异常: 查询参数不被支持,仅支持多个基本类型参数 或 者单个引用类型的参数并标记@EsQueryIndex注解");
        }
        return esAnnotationQueryFactory.getBean(queryEnum).handle(proxy, method, args);
    }


}
