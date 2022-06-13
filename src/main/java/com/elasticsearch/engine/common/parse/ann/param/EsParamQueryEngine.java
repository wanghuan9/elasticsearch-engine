package com.elasticsearch.engine.common.parse.ann.param;

import com.elasticsearch.engine.common.parse.ann.EsAnnQueryEngineCommon;
import com.elasticsearch.engine.common.parse.ann.QueryHandlerFactory;
import com.elasticsearch.engine.holder.AbstractEsRequestHolder;
import com.elasticsearch.engine.mapping.handler.AbstractQueryHandler;
import com.elasticsearch.engine.model.domain.EsQueryFieldBean;
import com.elasticsearch.engine.model.domain.EsQueryIndexBean;
import com.elasticsearch.engine.model.domain.ParamParserResultModel;

import java.lang.reflect.Method;

/**
 * @author wanghuan
 * @description: EsParamQueryEngine
 * @date 2022-01-26 11:28
 * @email 1078481395@qq.com
 */
public class EsParamQueryEngine extends EsAnnQueryEngineCommon {

    /**
     * 解析查询参数
     *
     * @param method
     * @param args
     * @return
     */
    public static AbstractEsRequestHolder execute(Method method, Object[] args) {
        QueryParamAnnParser translator = QueryParamAnnParser.instance();
        //解析类注解 index信息及包含的字段
        EsQueryIndexBean indexQueryBean = translator.getIndex(method);
        //解析具体的注解 字段,字段值,注解,查询类型
        ParamParserResultModel read = translator.read(method, args);
        //构建查询信息
        AbstractEsRequestHolder helper = AbstractEsRequestHolder.builder().config(indexQueryBean).build(read);
        for (EsQueryFieldBean queryDes : read.getQueryDesList()) {
            String queryKey = queryDes.getQueryType();
            //构建具体的查询处理Handler
            AbstractQueryHandler queryHandle = QueryHandlerFactory.getTargetHandleInstance(queryKey);
            //拼装查询语句逻辑具体执行
            queryHandle.execute(queryDes, helper);
        }
        //后置逻辑扩展
        enginePostProcessor(helper);
        return helper;
    }
   
}
