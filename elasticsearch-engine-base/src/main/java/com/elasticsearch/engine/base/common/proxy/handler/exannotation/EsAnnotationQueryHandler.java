package com.elasticsearch.engine.base.common.proxy.handler.exannotation;


import com.elasticsearch.engine.base.common.factory.MatchingBean;
import com.elasticsearch.engine.base.common.proxy.enums.EsAnnotationQueryEnum;

import java.lang.reflect.Method;

/**
 * @author wanghuan
 * @description: ROOD
 * @date 2022-04-15 18:15
 */
public interface EsAnnotationQueryHandler extends MatchingBean<EsAnnotationQueryEnum> {

    Object handle(Object proxy, Method method, Object[] args);
}
