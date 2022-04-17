package com.elasticsearch.engine.elasticsearchengine.common.parse.ann.model;

import com.elasticsearch.engine.elasticsearchengine.holder.AbstractEsRequestHolder;
import com.elasticsearch.engine.elasticsearchengine.mapping.handler.AbstractQueryHandler;
import com.elasticsearch.engine.elasticsearchengine.model.domain.EsQueryFieldBean;
import com.elasticsearch.engine.elasticsearchengine.model.domain.EsQueryIndexBean;
import com.elasticsearch.engine.elasticsearchengine.model.domain.ParamParserResultModel;
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
 * @email 1078481395@qq.com
 */
public class EsQueryEngine {

    /**
     * 解析查询参数
     *
     * @param queryViewObj
     * @param visitParent  return
     */
    public static AbstractEsRequestHolder execute(Object queryViewObj, boolean visitParent) {
        QueryAnnParser translator = QueryAnnParser.instance();
        //解析类注解 index信息及包含的字段
        EsQueryIndexBean indexQueryBean = translator.getIndex(queryViewObj);
        //解析具体的注解 字段,字段值,注解,查询类型
        ParamParserResultModel read = translator.read(queryViewObj, visitParent);
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

    /**
     * 后置逻辑扩展
     *
     * @param helper
     */
    private static void enginePostProcessor(AbstractEsRequestHolder helper) {
        //From To范围查询构建
        Map<Integer, RangeQueryBuilder> range = helper.getRange();
        if (Objects.nonNull(range) && !range.isEmpty()) {
            range.values().forEach(item -> {
                helper.chain(item);
            });
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
    }
}
