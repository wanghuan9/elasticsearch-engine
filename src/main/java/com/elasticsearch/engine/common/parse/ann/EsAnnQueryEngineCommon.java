package com.elasticsearch.engine.common.parse.ann;

import com.elasticsearch.engine.config.EsEngineConfig;
import com.elasticsearch.engine.holder.AbstractEsRequestHolder;
import org.apache.commons.lang3.math.NumberUtils;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.RangeQueryBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author wanghuan
 * @description: EsQueryEngine
 * @date 2022-01-26 11:28
 */
public class EsAnnQueryEngineCommon {

    /**
     * 后置逻辑扩展
     *
     * @param helper
     */
    protected static void enginePostProcessor(AbstractEsRequestHolder helper) {
        //From To范围查询构建
        Map<Integer, RangeQueryBuilder> range = helper.getRange();
        if (Objects.nonNull(range) && !range.isEmpty()) {
            range.values().forEach(helper::chain);
        }
        //包含should条件时 添加 minimumShouldMatch
        List<QueryBuilder> should = new ArrayList<>();
        BoolQueryBuilder query = null;
        QueryBuilder queryBuilder = helper.getQueryBuilder();
        if (queryBuilder instanceof BoolQueryBuilder) {
            query = (BoolQueryBuilder) helper.getQueryBuilder();
            should = query.should();
        }
        if (!should.isEmpty()) {
            query.minimumShouldMatch(NumberUtils.INTEGER_ONE);
        }
        //设置默认size
        int size = helper.getSource().size();
        if (size == -1) {
            helper.getSource().size(EsEngineConfig.getDefaultQuerySize());
        }
        //设置默认排序
    }
}
