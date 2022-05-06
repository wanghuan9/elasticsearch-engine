package com.elasticsearch.engine.elasticsearchengine.common.proxy.handler.impl;

import com.elasticsearch.engine.elasticsearchengine.common.proxy.enums.EsQueryType;
import com.elasticsearch.engine.elasticsearchengine.common.proxy.enums.EsSqlQueryEnum;
import com.elasticsearch.engine.elasticsearchengine.common.proxy.handler.EsQueryProxyExecuteHandler;
import com.elasticsearch.engine.elasticsearchengine.common.proxy.handler.exsql.EsSqlQueryFactory;
import com.elasticsearch.engine.elasticsearchengine.common.utils.ReflectionUtils;
import com.elasticsearch.engine.elasticsearchengine.common.utils.ThreadLocalUtil;
import com.elasticsearch.engine.elasticsearchengine.model.constant.CommonConstant;
import com.elasticsearch.engine.elasticsearchengine.model.exception.EsHelperQueryException;
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
        if (args == null || args.length == 0) {
            throw new EsHelperQueryException(prefix + "missing parameters");
        }
        //并且参数都是基础类型
        if (!ReflectionUtils.allParamIsBaseType(args)) {
            throw new EsHelperQueryException(prefix + "方法参数异常: 查询参数不被支持,仅支持多个基本类型参数 或 者单个引用类型的参数并标记@EsQueryIndex注解");
        }
        return esSqlQueryFactory.getBean(EsSqlQueryEnum.ANNOTATION_QUERY).handle(proxy, method, args);

    }


}
