package com.elasticsearch.engine.elasticsearchengine.common.parse.ann.model;

import com.elasticsearch.engine.elasticsearchengine.common.GlobalConfig;
import com.elasticsearch.engine.elasticsearchengine.common.utils.JsonParser;
import com.elasticsearch.engine.elasticsearchengine.model.domain.BaseHit;
import com.elasticsearch.engine.elasticsearchengine.model.domain.BaseResp;
import com.elasticsearch.engine.elasticsearchengine.model.emenu.JsonNamingStrategy;
import com.elasticsearch.engine.elasticsearchengine.model.exception.EsHelperQueryException;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author wanghuan
 * @description: EsResponseParse
 * @date 2022-01-26 11:28
 * @email 1078481395@qq.com
 */
public class EsResponseParse {

    /**
     * phrase SearchResponse and return Result
     *
     * @param resp return
     */
    public static <T> BaseResp<T> returnDefaultResult(SearchResponse resp, Class responseClazz) {
        BaseResp baseResp;
        try {
            if (BaseHit.class.isAssignableFrom(responseClazz)) {
                baseResp = EsResponseParse.getListBaseHit(resp, responseClazz);
            } else {
                baseResp = EsResponseParse.getList(resp, responseClazz);
            }
        } catch (Exception e) {
            throw new EsHelperQueryException("convert result error, cause:", e);
        }
        return baseResp;
    }

    /**
     * 获取list结果
     *
     * @param resp
     * @param type
     * @param <T>
     * @return
     */
    public static <T extends BaseHit> BaseResp<T> getListBaseHit(SearchResponse resp, Class<T> type) {
        BaseResp<T> res = new BaseResp<>();
        SearchHits hits = resp.getHits();
        res.setTotalHit(hits.getTotalHits().value);
        SearchHit[] hitArr = hits.getHits();
        List<T> records = Arrays.stream(hitArr).map(item -> {
            String sourceAsString = item.getSourceAsString();
            T t = jsonStrToObject(sourceAsString, type);
            return t;
        }).collect(Collectors.toList());
        res.setRecords(records);
        return res;
    }

    /**
     * 获取list结果
     *
     * @param resp
     * @param type
     * @param <T>
     * @return
     */
    public static <T> BaseResp<T> getList(SearchResponse resp, Class<T> type) {
        BaseResp<T> res = new BaseResp<>();
        SearchHits hits = resp.getHits();
        res.setTotalHit(hits.getTotalHits().value);
        SearchHit[] hitArr = hits.getHits();
        List<T> records = Arrays.stream(hitArr).map(item -> {
            String sourceAsString = item.getSourceAsString();
            T t = jsonStrToObject(sourceAsString, type);
            return t;
        }).collect(Collectors.toList());
        res.setRecords(records);
        return res;
    }

    /**
     * 获取单个结构
     *
     * @param resp
     * @param type
     * @param <T>
     * @return
     */
    public static <T extends BaseHit> Optional<T> getOne(SearchResponse resp, Class<T> type) {
        SearchHits hits = resp.getHits();
        SearchHit[] hitArr = hits.getHits();
        if (hitArr.length > 1) {
            throw new EsHelperQueryException("except one result, but find more");
        }
        if (hitArr.length == 1) {
            String jsonResStr = hitArr[0].getSourceAsString();
            T t = jsonStrToObject(jsonResStr, type);
            t.setDocId(hitArr[0].getId());
            return Optional.of(t);
        }
        return Optional.empty();
    }

    /**
     * json string to object
     *
     * @param jsonString
     * @param type
     * @param <T>
     * @return
     */
    private static <T> T jsonStrToObject(String jsonString, Class<T> type) {
        if (GlobalConfig.RES_PROPERTY_NAMING_STRATEGY.equals(JsonNamingStrategy.SNAKE_CASE)) {
            return JsonParser.asObjectSnakeCase(jsonString, type);
        } else {
            return JsonParser.asObject(jsonString, type);
        }
    }
}
