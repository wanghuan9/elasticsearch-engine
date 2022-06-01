package com.elasticsearch.engine.common.proxy.handler.exsql;

import com.elasticsearch.engine.common.factory.MatchingBean;
import com.elasticsearch.engine.common.proxy.enums.EsSqlQueryEnum;

import java.lang.reflect.Method;

/**
 * @author wanghuan
 * @description: ROOD
 * @date 2022-04-15 18:15
 */
public interface EsSqlQueryHandler extends MatchingBean<EsSqlQueryEnum> {

    Object handle(Object proxy, Method method, Object[] args);
}
