package com.elasticsearch.engine.common.proxy.handler.impl;

import com.elasticsearch.engine.common.proxy.handler.exsql.EsSqlQueryFactory;
import com.elasticsearch.engine.common.utils.ReflectionUtils;
import com.elasticsearch.engine.common.utils.ThreadLocalUtil;
import com.elasticsearch.engine.common.proxy.enums.EsQueryType;
import com.elasticsearch.engine.common.proxy.enums.EsSqlQueryEnum;
import com.elasticsearch.engine.common.proxy.handler.EsQueryProxyExecuteHandler;
import com.elasticsearch.engine.model.constant.CommonConstant;
import com.elasticsearch.engine.model.exception.EsEngineQueryException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.lang.reflect.Method;

/**
 * @author wanghuan
 * @description: es sql查询代理实现逻辑
 * @date 2022-04-15 18:10
 */
@Slf4j
@Component
public class EsSqlQuery implements EsQueryProxyExecuteHandler {

    @Resource
    private EsSqlQueryFactory esSqlQueryFactory;

    @Override
    public Boolean matching(EsQueryType factory) {
        return EsQueryType.SQL.equals(factory);
    }

    @Override
    public Object handle(Object proxy, Method method, Object[] args) {
        String prefix = ThreadLocalUtil.get(CommonConstant.INTERFACE_METHOD_NAME);
        if (args != null && args.length > 0) {
            //参数类型校验
            if (!ReflectionUtils.allParamIsBaseType(args)) {
                throw new EsEngineQueryException(prefix + "方法参数异常: 查询参数不被支持,仅支持多个基本类型参数 或 者单个引用类型的参数并标记@EsQueryIndex注解");
            }
        }
       
        return esSqlQueryFactory.getBean(EsSqlQueryEnum.ANNOTATION_QUERY).handle(proxy, method, args);

    }


}
