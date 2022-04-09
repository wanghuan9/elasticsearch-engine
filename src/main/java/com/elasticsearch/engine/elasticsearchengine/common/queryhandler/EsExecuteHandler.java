package com.elasticsearch.engine.elasticsearchengine.common.queryhandler;

import com.elasticsearch.engine.elasticsearchengine.common.parse.QueryAnnParser;
import com.elasticsearch.engine.elasticsearchengine.common.utils.JsonParser;
import com.elasticsearch.engine.elasticsearchengine.mapping.annotation.Aggs;
import com.elasticsearch.engine.elasticsearchengine.model.constant.EsConstant;
import com.elasticsearch.engine.elasticsearchengine.model.domain.BaseResp;
import com.elasticsearch.engine.elasticsearchengine.model.domain.DefaultAggResp;
import com.elasticsearch.engine.elasticsearchengine.model.exception.EsHelperQueryException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author wanghuan
 * @description: es 搜索工具类
 * @date 2021-09-29
 * @time 10:33
 */
@Slf4j
@Component
public class EsExecuteHandler extends EsBaseExecuteHandle {
    @Resource
    private RestHighLevelClient restClient;

//    /**
//     * 分页查询
//     *
//     * @param param         需要解析的查询实体
//     * @param responseClazz 查询结果实体类型
//     * @param <T>           查询结果实体类型对应的泛型
//     * @param <U>           请求参数对应的泛型
//     * @return
//     */
//    public <T, U extends PageRequest> BaseResp<T> executePage(U param, Class<T> responseClazz) {
//        AbstractEsRequestHolder esHolder = EsQueryEngine.execute(param, Boolean.TRUE);
//        return baseExecute(param, responseClazz, esHolder);
//    }

    /**
     * 分页查询扩展结果构建
     *
     * @param param           需要解析的查询实体
     * @param K               es查询结果实体类型
     * @param convertFunction 查询结果转换函数
     * @param <K>             es查询结果实体类型对应的泛型
     * @param <T>             转换函数结果实体类型对应的泛型
     * @param <U>             请求参数对应的泛型
     * @return
     */
//    public <K, T, U extends PageRequest> RestPageDataResponse<T> executePage(U param, Class<K> K, Function<List<K>,List<T>> convertFunction) {
//        AbstractEsRequestHolder esHolder = EsQueryEngine.execute(param, Boolean.TRUE);
//        BaseResp<K> res = baseExecute(param, K, esHolder);
//        return Page.toPage(param, res::getTotalHit, () -> convertFunction.apply(res.getRecords())).toResponse();
//    }

    /**
     * List查询并构建结果
     *
     * @param param         需要解析的查询实体
     * @param responseClazz 查询结果实体类型
     * @param <T>           查询结果实体类型对应的泛型
     * @return
     */
    public <T> List<T> executeList(Object param, Class<T> responseClazz) {
        BaseResp<T> result = execute(param, responseClazz);
        return result.getRecords();
    }

    /**
     * 单个查询并构建结果
     * 若查询结果>1 会抛异常
     *
     * @param param         需要解析的查询实体
     * @param responseClazz 查询结果实体类型
     * @param <T>           查询结果实体类型对应的泛型
     * @return
     */
    public <T> T executeOne(Object param, Class<T> responseClazz) {
        BaseResp<T> result = execute(param, responseClazz);
        if (CollectionUtils.isEmpty(result.getRecords())) {
            return null;
        }
        if (result.getRecords().size() > 1) {
            throw new EsHelperQueryException("except one result, but find more");
        }
        return result.getRecords().stream().findFirst().get();
    }


    /**
     * 校验是否存在分组查询注解
     * 构建默认分组查询结果
     *
     * @param param 需要解析的查询实体
     * @return
     */
    public BaseResp<DefaultAggResp> executeAggs(Object param) {
        if (!checkExistsAggAnnotation(param)) {
            throw new EsHelperQueryException("param field Missing @Aggs annotation");
        }
        List<DefaultAggResp> records = new ArrayList<>();
        SearchResponse searchResponse = execute(param);
        if (Objects.isNull(searchResponse.getAggregations())) {
            throw new EsHelperQueryException("aggs param value is null, result aggregations is empty");
        }
        Terms agg = searchResponse.getAggregations().get(EsConstant._AGG);
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
