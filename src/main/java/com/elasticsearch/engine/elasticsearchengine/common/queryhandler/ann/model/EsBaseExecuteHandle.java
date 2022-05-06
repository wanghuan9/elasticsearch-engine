package com.elasticsearch.engine.elasticsearchengine.common.queryhandler.ann.model;

import com.elasticsearch.engine.elasticsearchengine.common.GlobalConfig;
import com.elasticsearch.engine.elasticsearchengine.common.parse.ann.model.EsQueryEngine;
import com.elasticsearch.engine.elasticsearchengine.common.parse.ann.model.EsResponseParse;
import com.elasticsearch.engine.elasticsearchengine.common.utils.JsonParser;
import com.elasticsearch.engine.elasticsearchengine.common.utils.ReflectionUtils;
import com.elasticsearch.engine.elasticsearchengine.holder.AbstractEsRequestHolder;
import com.elasticsearch.engine.elasticsearchengine.hook.RequestHook;
import com.elasticsearch.engine.elasticsearchengine.hook.ResponseHook;
import com.elasticsearch.engine.elasticsearchengine.model.domain.BaseResp;
import com.elasticsearch.engine.elasticsearchengine.model.exception.EsHelperQueryException;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.core.TimeValue;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author wanghuan
 * @description: es 搜索工具类
 * @date 2021-09-29
 * @time 10:33
 */
@Slf4j
@Component
public class EsBaseExecuteHandle {
    @Resource
    private RestHighLevelClient restClient;

    /**
     * 原生查询
     *
     * @param sourceBuilder 原生查询条件
     * @param indexName     索引名
     * @return
     */
    public SearchResponse execute(SearchSourceBuilder sourceBuilder, String indexName) {
        SearchResponse searchResponse;
        //设置超时时间
        sourceBuilder.timeout(new TimeValue(GlobalConfig.QUERY_TIME_OUT, TimeUnit.SECONDS));
        //ES的查询请求对象
        SearchRequest searchRequest = new SearchRequest().indices(indexName).source(sourceBuilder);
        log.info("execute-es-query-json is\n{}", searchRequest);
        try {
            searchResponse = restClient.search(searchRequest, RequestOptions.DEFAULT);
        } catch (IOException e) {
            throw new EsHelperQueryException("Execute Query Error, Method-invokeNative ,cause:", e);
        }
        SearchHits hitsResult = searchResponse.getHits();
        log.info("命中总记录数 ={}", hitsResult.getTotalHits());
        return searchResponse;
    }

    /**
     * 原生查询 构建结果
     *
     * @param sourceBuilder 原生查询条件
     * @param indexName     索引名
     * @param responseClazz 查询结果实体类型
     * @param <T>           查询结果实体类型对应的泛型
     * @return
     */
    public <T> BaseResp<T> execute(SearchSourceBuilder sourceBuilder, String indexName, Class<T> responseClazz) {
        SearchResponse searchResponse;
        //设置超时时间
        sourceBuilder.timeout(new TimeValue(GlobalConfig.QUERY_TIME_OUT, TimeUnit.SECONDS));
        //ES的查询请求对象
        SearchRequest searchRequest = new SearchRequest().indices(indexName).source(sourceBuilder);
        log.info("execute-es-query-json is\n{}", searchRequest);
        try {
            searchResponse = restClient.search(searchRequest, RequestOptions.DEFAULT);
        } catch (IOException e) {
            throw new EsHelperQueryException("Execute Query Error, Method-invokeNativeBuildRes ,cause:", e);
        }
        SearchHits hitsResult = searchResponse.getHits();
        log.info("命中总记录数 ={}", hitsResult.getTotalHits());
        return EsResponseParse.returnDefaultResult(searchResponse, responseClazz);
    }

    /**
     * 代理查询 不构建结果
     *
     * @param param 需要解析的查询实体
     * @return
     */
    public SearchResponse execute(Object param) {
        SearchResponse resp;
        AbstractEsRequestHolder esHolder = EsQueryEngine.execute(param, Boolean.FALSE);
        SearchSourceBuilder source = esHolder.getSource();
        //设置超时时间
        source.timeout(new TimeValue(GlobalConfig.QUERY_TIME_OUT, TimeUnit.SECONDS));
        //前置扩展
        executePostProcessorBefore(param, esHolder);
        log.info("execute-es-query-json is\n{}", source);
        try {
            resp = restClient.search(esHolder.getRequest(), RequestOptions.DEFAULT);
        } catch (IOException e) {
            throw new EsHelperQueryException("Execute Query Error, Method-invoke ,cause:", e);
        }
        log.info("execute-es-response-json is\n{}", JsonParser.asJson(resp));
        return resp;
    }

    /**
     * 代理查询并构建结果(自定义扩展查询-兼容invoke)
     *
     * @param param         需要解析的查询实体
     * @param responseClazz 查询结果实体类型
     * @param <T>           查询结果实体类型对应的泛型
     * @return
     */
    public <T> BaseResp<T> execute(Object param, Class responseClazz) {
        AbstractEsRequestHolder esHolder = EsQueryEngine.execute(param, Boolean.FALSE);
        return baseExecute(param, responseClazz, esHolder);
    }


    /**
     * 获取构建的查询条件
     *
     * @param param 需要解析的查询实体
     * @return
     */
    public SearchSourceBuilder getSearchSourceBuilder(Object param) {
        AbstractEsRequestHolder esHolder = EsQueryEngine.execute(param, Boolean.FALSE);
        return esHolder.getSource();
    }


    /**
     * 执行查询构建结果基础通用方法
     *
     * @param param
     * @param responseClazz
     * @param esHolder
     * @param <T>
     * @return
     */
    protected <T> BaseResp<T> baseExecute(Object param, Class<T> responseClazz, AbstractEsRequestHolder esHolder) {
        SearchResponse resp;
        SearchSourceBuilder source = esHolder.getSource();
        //设置超时时间
        source.timeout(new TimeValue(GlobalConfig.QUERY_TIME_OUT, TimeUnit.SECONDS));
        //前置扩展
        executePostProcessorBefore(param, esHolder);
        log.info("execute-es-query-json is\n{}", esHolder.getSource().toString());
        try {
            resp = restClient.search(esHolder.getRequest(), RequestOptions.DEFAULT);
        } catch (IOException e) {
            throw new EsHelperQueryException("Execute Query Error, Method-invokeRes ,cause:", e);
        }
        log.info("execute-es-response-json is\n{}", JsonParser.asJson(resp));
        //后置处理扩展 加入自定义结果解析
        //TODO 扩展默认分组结果构建
        ResponseHook<T> responseHook;
        BaseResp<T> result = new BaseResp<>();
        if ((responseHook = checkResponseHook(param)) != null) {
            result.setResult(responseHook.handleResponse(resp));
        } else {
            result = EsResponseParse.returnDefaultResult(resp, responseClazz);
        }
        log.info("execute-es-result-json is\n{}", JsonParser.asJson(result));
        return result;
    }

    /**
     * execute 前置处理
     *
     * @param param
     * @param esHolder
     */
    private void executePostProcessorBefore(Object param, AbstractEsRequestHolder esHolder) {
        //前置处理扩展 嵌套扩展处理
        List<Object> hooks = esHolder.getRequestHooks();
        if (!hooks.isEmpty()) {
            for (Object obj : hooks) {
                RequestHook requestHook = (RequestHook) obj;
                esHolder = requestHook.handleRequest(esHolder, obj);
            }
        }
        //前置处理扩展 继承扩展处理
        List<RequestHook> requestHooks = checkRequestHook(param);
        if (!requestHooks.isEmpty()) {
            for (RequestHook requestHook : requestHooks) {
                esHolder = requestHook.handleRequest(esHolder, param);
            }
        }
    }

    private void executePostProcessorAfter() {

    }

    protected List<RequestHook> checkRequestHook(Object param) {
        List<RequestHook> requestHooks = new ArrayList<>();
        List<Class<?>> superClass = ReflectionUtils.getSuperClass(param.getClass());
        superClass.add(param.getClass());
        for (Class<?> clazz : superClass) {
            if (RequestHook.class.isAssignableFrom(clazz)) {
                try {
                    requestHooks.add((RequestHook) clazz.newInstance());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return requestHooks;
    }

    protected ResponseHook checkResponseHook(Object param) {
        if (ResponseHook.class.isAssignableFrom(param.getClass())) {
            return (ResponseHook) param;
        }
        return null;
    }
}
