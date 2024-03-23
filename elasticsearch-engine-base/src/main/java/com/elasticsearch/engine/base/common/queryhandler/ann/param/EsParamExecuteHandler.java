package com.elasticsearch.engine.base.common.queryhandler.ann.param;

import com.elasticsearch.engine.base.common.parse.ann.EsResponseParse;
import com.elasticsearch.engine.base.common.parse.ann.param.EsParamQueryEngine;
import com.elasticsearch.engine.base.common.queryhandler.ann.model.AbstractEsBaseExecuteHandle;
import com.elasticsearch.engine.base.common.utils.JsonParser;
import com.elasticsearch.engine.base.common.utils.ThreadLocalUtil;
import com.elasticsearch.engine.base.config.EsEngineConfig;
import com.elasticsearch.engine.base.holder.AbstractEsRequestHolder;
import com.elasticsearch.engine.base.model.constant.CommonConstant;
import com.elasticsearch.engine.base.model.domain.BaseResp;
import com.elasticsearch.engine.base.model.exception.EsEngineQueryException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author wanghuan
 * @description: param 注解查询执行器
 * @date 2022-04-16 22:54
 */
@Slf4j
@Component
public class EsParamExecuteHandler extends AbstractEsBaseExecuteHandle {

    @Resource
    private RestHighLevelClient restClient;

    @Override
    public void executePostProcessorBefore(Object param, AbstractEsRequestHolder esHolder) {
        String methodName = ThreadLocalUtil.get(CommonConstant.INTERFACE_METHOD_NAME);
        //前置处理es索引名动态配置
        resetIndexName(esHolder);
        if (EsEngineConfig.getQueryJsonLog()) {
            log.info("{} execute-es-query-json is\n{}", methodName, esHolder.getSource().toString());
        }
    }

    @Override
    public <T> void executePostProcessorAfter(Object param, SearchResponse resp, BaseResp<T> result) {
        String methodName = ThreadLocalUtil.get(CommonConstant.INTERFACE_METHOD_NAME);
        if (EsEngineConfig.getQueryJsonLog()) {
            log.info("{} execute-es-result-json is\n{}", methodName, JsonParser.asJson(result));
        }
    }


    /**
     * @param method        查询的方法
     * @param args          查询方法对应的参数值
     * @param responseClazz 查询结果实体类型
     * @param <T>           查询结果实体类型对应的泛型
     * @return
     */
    public <T> List<T> executeList(Method method, Object[] args, Class<T> responseClazz) {
        BaseResp<T> result = execute(method, args, responseClazz);
        return result.getRecords();
    }

    /**
     * @param method        查询的方法
     * @param args          查询方法对应的参数值
     * @param responseClazz 查询结果实体类型
     * @param <T>           查询结果实体类型对应的泛型
     * @return
     */
    public <T> T executeOne(Method method, Object[] args, Class<T> responseClazz) {
        BaseResp<T> result = execute(method, args, responseClazz);
        if (CollectionUtils.isEmpty(result.getRecords())) {
            return null;
        }
        if (result.getRecords().size() > 1) {
            throw new EsEngineQueryException("except one result, but find more");
        }
        return result.getRecords().stream().findFirst().get();
    }

    /**
     * @param method        查询的方法
     * @param args          查询方法对应的参数值
     * @param responseClazz 查询结果实体类型
     * @param <T>           查询结果实体类型对应的泛型
     * @return
     */
    public <T> BaseResp<T> execute(Method method, Object[] args, Class responseClazz) {
        AbstractEsRequestHolder esHolder = EsParamQueryEngine.execute(method, args);
        return baseExecute(responseClazz, esHolder);
    }

    /**
     * 执行查询构建结果基础通用方法
     *
     * @param responseClazz
     * @param esHolder
     * @param <T>
     * @return
     */
    protected <T> BaseResp<T> baseExecute(Class<T> responseClazz, AbstractEsRequestHolder esHolder) {
        try {
            SearchResponse resp;
            SearchSourceBuilder source = esHolder.getSource();
            String methodName = ThreadLocalUtil.get(CommonConstant.INTERFACE_METHOD_NAME);
            //设置超时时间
            source.timeout(new TimeValue(EsEngineConfig.getQueryTimeOut(), TimeUnit.SECONDS));
            //前置扩展
            executePostProcessorBefore(null, esHolder);
            try {
                resp = restClient.search(esHolder.getRequest(), RequestOptions.DEFAULT);
            } catch (IOException e) {
                throw new EsEngineQueryException("Execute Query Error, Method-invokeRes ,cause:", e);
            }
            //后置处理扩展 加入自定义结果解析
            BaseResp<T> result = EsResponseParse.returnDefaultResult(resp, responseClazz);
            executePostProcessorAfter(null, resp, result);
            ThreadLocalUtil.remove();
            return result;
        } finally {
            ThreadLocalUtil.remove(CommonConstant.INTERFACE_METHOD_NAME);
        }
    }
}
