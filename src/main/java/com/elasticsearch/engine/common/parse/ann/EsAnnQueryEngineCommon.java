package com.elasticsearch.engine.common.parse.ann;

import com.elasticsearch.engine.config.EsEngineConfig;
import com.elasticsearch.engine.holder.AbstractEsRequestHolder;
import com.elasticsearch.engine.mapping.annotation.method.Exclude;
import com.elasticsearch.engine.mapping.annotation.method.Include;
import com.elasticsearch.engine.mapping.annotation.method.Order;
import com.elasticsearch.engine.mapping.annotation.method.Size;
import com.elasticsearch.engine.model.constant.CommonConstant;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.SortBuilder;

import java.lang.reflect.Method;
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
    protected static void enginePostProcessor(AbstractEsRequestHolder helper, Method method) {
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
        buildDefaultSize(helper,method);
        //设置默认排序
        buildDefaultSort(helper, method);
        //设置查询字段
        buildDefaultSourceFiled(helper, method);
    }

    /**
     * 解析方法上的size注解 及size为空时设置默认size
     * @param helper
     * @param method
     */
    public static void buildDefaultSize(AbstractEsRequestHolder helper, Method method){
        //设置默认size
        int size = helper.getSource().size();
        if (size == -1) {
            Size annSize = method.getAnnotation(Size.class);
            if(Objects.nonNull(annSize)){
                size = annSize.value();
            }else {
                size = EsEngineConfig.getDefaultQuerySize();
            }
            helper.getSource().size(size);
        }
    }

    /**
     * 解析方法上的order注解 及order为空时设置默认preference
     * @param helper
     * @param method
     */
    public static void buildDefaultSort(AbstractEsRequestHolder helper, Method method){
        //设置默认sort
        List<SortBuilder<?>> sorts = helper.getSource().sorts();
        Order annSort = method.getAnnotation(Order.class);
        if(Objects.nonNull(annSort)){
            SearchSourceBuilder source = helper.getSource();
            source.sort(annSort.value(), annSort.type());
        }
        if(CollectionUtils.isEmpty(sorts)){
            helper.getRequest().preference(CommonConstant.DEFAULT_PREFERENCE);
        }
    }

    /**
     * 解析方法上的include,exclude注解 设置要查询的字段
     * @param helper
     * @param method
     */
    public static void buildDefaultSourceFiled(AbstractEsRequestHolder helper, Method method){
        //设置默认source filed
        String[] includeValue = {};
        String[] excludeValue = {};
        Include include = method.getAnnotation(Include.class);
        Exclude exclude = method.getAnnotation(Exclude.class);
        if(Objects.nonNull(include)){
            includeValue = include.value();
        }
        if(Objects.nonNull(exclude)){
            excludeValue = exclude.value();
        }
        helper.getSource().fetchSource(includeValue, excludeValue);
    }
}
