package com.elasticsearch.engine.base.common.queryhandler.ann.model;

import com.elasticsearch.engine.base.common.parse.ann.EsResponseParse;
import com.elasticsearch.engine.base.common.parse.ann.model.EsQueryEngine;
import com.elasticsearch.engine.base.common.parse.ann.model.QueryAnnParser;
import com.elasticsearch.engine.base.common.utils.JsonParser;
import com.elasticsearch.engine.base.config.EsEngineConfig;
import com.elasticsearch.engine.base.holder.AbstractEsRequestHolder;
import com.elasticsearch.engine.base.hook.RequestHook;
import com.elasticsearch.engine.base.hook.ResponseHook;
import com.elasticsearch.engine.base.mapping.annotation.Aggs;
import com.elasticsearch.engine.base.model.constant.EsConstant;
import com.elasticsearch.engine.base.model.domain.BaseResp;
import com.elasticsearch.engine.base.model.domain.DefaultAggResp;
import com.elasticsearch.engine.base.model.exception.EsEngineQueryException;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * @author wanghuan
 * @description: es 搜索工具类
 * @date 2021-09-29
 * @time 10:33
 */
@Slf4j
@Component
public class EsBaseExecuteHandle extends AbstractEsBaseExecuteHandle {

    @Resource
    private RestHighLevelClient restClient;

    /**
     * execute 前置处理
     *
     * @param param
     * @param esHolder
     */
    @Override
    public void executePostProcessorBefore(Object param, AbstractEsRequestHolder esHolder) {
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
        //前置处理es索引名动态配置
        resetIndexName(esHolder);
        log.info("execute-es-query-json is\n{}", esHolder.getSource().toString());
    }

    @Override
    public <T> void executePostProcessorAfter(Object param, SearchResponse resp, BaseResp<T> result) {
        ResponseHook<T> responseHook;
        if ((responseHook = checkResponseHook(param)) != null) {
            result.setResult(responseHook.handleResponse(resp));
        }
        log.info("execute-es-result-json is\n{}", JsonParser.asJson(result));
    }


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
        sourceBuilder.timeout(new TimeValue(EsEngineConfig.getQueryTimeOut(), TimeUnit.SECONDS));
        //ES的查询请求对象
        SearchRequest searchRequest = new SearchRequest().indices(indexName).source(sourceBuilder);
        log.info("execute-es-query-json is\n{}", searchRequest);
        try {
            searchResponse = restClient.search(searchRequest, RequestOptions.DEFAULT);
        } catch (IOException e) {
            throw new EsEngineQueryException("Execute Query Error, Method-invokeNative ,cause:", e);
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
        sourceBuilder.timeout(new TimeValue(EsEngineConfig.getQueryTimeOut(), TimeUnit.SECONDS));
        //ES的查询请求对象
        SearchRequest searchRequest = new SearchRequest().indices(indexName).source(sourceBuilder);
        log.info("execute-es-query-json is\n{}", searchRequest);
        try {
            searchResponse = restClient.search(searchRequest, RequestOptions.DEFAULT);
        } catch (IOException e) {
            throw new EsEngineQueryException("Execute Query Error, Method-invokeNativeBuildRes ,cause:", e);
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
        return execute(null, param);
    }

    /**
     * 代理查询 不构建结果
     *
     * @param param 需要解析的查询实体
     * @return
     */
    public SearchResponse execute(Method method, Object param) {
        SearchResponse resp;
        AbstractEsRequestHolder esHolder = EsQueryEngine.execute(method, param, EsEngineConfig.isVisitQueryBeanParent());
        SearchSourceBuilder source = esHolder.getSource();
        //设置超时时间
        source.timeout(new TimeValue(EsEngineConfig.getQueryTimeOut(), TimeUnit.SECONDS));
        //前置扩展
        executePostProcessorBefore(param, esHolder);
        try {
            resp = restClient.search(esHolder.getRequest(), RequestOptions.DEFAULT);
        } catch (IOException e) {
            throw new EsEngineQueryException("Execute Query Error, Method-invoke ,cause:", e);
        }
        return resp;
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
        source.timeout(new TimeValue(EsEngineConfig.getQueryTimeOut(), TimeUnit.SECONDS));
        //前置扩展
        executePostProcessorBefore(param, esHolder);
        try {
            resp = restClient.search(esHolder.getRequest(), RequestOptions.DEFAULT);
        } catch (IOException e) {
            throw new EsEngineQueryException("Execute Query Error, Method-invokeRes ,cause:", e);
        }
        //后置处理扩展 加入自定义结果解析
        BaseResp<T> result = EsResponseParse.returnDefaultResult(resp, responseClazz);
        executePostProcessorAfter(param, resp, result);
        return result;
    }


    /**
     * 校验是否存在分组查询注解
     * 构建默认分组查询结果
     *
     * @param param 需要解析的查询实体
     * @return
     */
    public BaseResp<DefaultAggResp> executeAggs(Method method, Object param) {
        if (!checkExistsAggAnnotation(param)) {
            throw new EsEngineQueryException("param field Missing @Aggs annotation");
        }
        List<DefaultAggResp> records = new ArrayList<>();
        SearchResponse searchResponse = execute(method, param);
        if (Objects.isNull(searchResponse.getAggregations())) {
            throw new EsEngineQueryException("aggs param value is null, result aggregations is empty");
        }
        Terms agg = searchResponse.getAggregations().get(EsConstant.AGG);
        for (Terms.Bucket bucketOneAgg : agg.getBuckets()) {
            DefaultAggResp defaultAgg = new DefaultAggResp();
            defaultAgg.setKey(bucketOneAgg.getKeyAsString());
            defaultAgg.setCount(bucketOneAgg.getDocCount());
            records.add(defaultAgg);
        }
        log.info("execute-es-response-json is\n{}", JsonParser.asJson(searchResponse));
        BaseResp<DefaultAggResp> resp = new BaseResp<>();
        resp.setRecords(records);
        resp.setTotalHit((long) records.size());
        log.info("execute-es-result-json is\n{}", JsonParser.asJson(resp));
        return resp;
    }

    /**
     * 校验是否存在分组查询注解
     * 构建默认分组查询结果
     *
     * @param param 需要解析的查询实体
     * @return
     */
    public BaseResp<DefaultAggResp> executeAggs(Object param) {
        return executeAggs(null, param);
    }

    /**
     * 检查是否存在 @aggs注解
     *
     * @param param
     * @return
     */
    protected Boolean checkExistsAggAnnotation(Object param) {
        List<Field> fields = QueryAnnParser.getFields(param.getClass(), Boolean.TRUE);
        for (Field field : fields) {
            if (field.isAnnotationPresent(Aggs.class)) {
                return true;
            }
        }
        return false;
    }
}
