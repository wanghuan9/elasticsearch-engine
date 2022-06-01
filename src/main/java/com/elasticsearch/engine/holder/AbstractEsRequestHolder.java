package com.elasticsearch.engine.holder;


import com.elasticsearch.engine.common.utils.ReflectionUtils;
import com.elasticsearch.engine.model.domain.EsQueryIndexBean;
import com.elasticsearch.engine.model.domain.ParamParserResultModel;
import com.elasticsearch.engine.model.emenu.EsConnector;
import com.elasticsearch.engine.model.emenu.QueryModel;
import com.google.common.collect.Maps;
import lombok.Data;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author wanghuan
 * @description: hold es request bean
 * @date 2022-01-26 11:28
 */
@Data
public abstract class AbstractEsRequestHolder<T extends QueryBuilder> {

    private static final Map<QueryModel, Class<? extends AbstractEsRequestHolder>> HOLDER_CLAZZ_MAP = Maps.newHashMap();

    /**
     * 初始化询构造对象
     */
    static {
        AbstractEsRequestHolder.regisHolder(QueryModel.BOOL, BoolEsRequestHolder.class);
        AbstractEsRequestHolder.regisHolder(QueryModel.DIS_MAX, DisMaxEsRequestHolder.class);
    }

    /**
     * 索引名
     */
    private String indexName;
    /**
     * request
     */
    private SearchRequest request;
    /**
     * source
     */
    private SearchSourceBuilder source;
    /**
     * range扩展
     */
    private Map<Integer, RangeQueryBuilder> range = new HashMap<>();
    /**
     * 嵌套查询扩展
     */
    private List<Object> requestHooks;
    /**
     * 初始化 queryBuilder和currentQueryBuilderList 再对应的 xxxEsRequestHolder 类中
     */
    //这里就是一个具体的BoolQueryBuilder,或者DisMaxQueryBuilder.  所以添加到 这个currentQueryBuilderList中 queryBuilder中自然就有值了
    private T queryBuilder;
    /**
     * @see BoolEsRequestHolder#defineDefaultLogicConnector()
     */
    //这里的currentQueryBuilderList 是 调用 org.elasticsearch.index.query.BoolQueryBuilder.must() 空参方法返回的,
    private List<QueryBuilder> currentQueryBuilderList;

    /**
     * 注册询构造对象
     *
     * @param model
     * @param clazz
     */
    public static void regisHolder(QueryModel model, Class<? extends AbstractEsRequestHolder> clazz) {
        HOLDER_CLAZZ_MAP.put(model, clazz);
    }

    public static EsRequestHolderBuilder builder() {
        return new EsRequestHolderBuilder();
    }

    /**
     * @param index return
     */
    public AbstractEsRequestHolder init(String index, List<Object> requestHooks) {
        this.indexName = index;
        this.requestHooks = requestHooks;
        request = new SearchRequest(index);
        source = new SearchSourceBuilder();
        this.defineQueryBuilder();
        source.query(queryBuilder);
        request.source(source);
        this.defineDefaultLogicConnector();
        return this;
    }

    /**
     * change logic connector
     *
     * @param connector return
     */
    public abstract AbstractEsRequestHolder changeLogicConnector(EsConnector connector);

    /**
     * 定义bool默认的连接方式 must/must_not/filter/should
     */
    protected abstract void defineDefaultLogicConnector();

    public AbstractEsRequestHolder chain(QueryBuilder queryBuilder) {
        this.currentQueryBuilderList.add(queryBuilder);
        return this;
    }

    /**
     * 定义查询构造对象 是boolQuery, dis-max 等
     */
    protected abstract void defineQueryBuilder();

    /**
     * @param queryBuilder
     * @TODO 扩展校验
     */
    public void addQueryBuilder(QueryBuilder queryBuilder) {
        this.currentQueryBuilderList.add(queryBuilder);
    }

    /**
     * @param queryBuilder
     * @TODO 扩展校验
     */
    public void addQueryBuilder(EsConnector connector, QueryBuilder queryBuilder) {
        changeLogicConnector(connector);
        this.currentQueryBuilderList.add(queryBuilder);
    }

    /**
     * EsRequestHolderBuilder
     */
    public static class EsRequestHolderBuilder {

        public String indexName;
        public QueryModel esQueryModel;
        public String[] includeFields;
        public String[] excludeFields;

        public EsRequestHolderBuilder config(EsQueryIndexBean indexBean) {
            this.indexName = indexBean.getIndexName();
            this.esQueryModel = indexBean.getEsQueryModel();
            this.includeFields = indexBean.getIncludeFields();
            return this;
        }

        public EsRequestHolderBuilder indexName(String indexName) {
            this.indexName = indexName;
            return this;
        }

        public EsRequestHolderBuilder queryModel(QueryModel model) {
            this.esQueryModel = model;
            return this;
        }

        public <R extends AbstractEsRequestHolder> R build(ParamParserResultModel read) {
            if (StringUtils.isBlank(indexName) || esQueryModel == null) {
                throw new RuntimeException("index and query model cant be null");
            }
            Class<? extends AbstractEsRequestHolder> targetClazz = HOLDER_CLAZZ_MAP.get(esQueryModel);
            if (Objects.nonNull(targetClazz)) {
                AbstractEsRequestHolder holder = ReflectionUtils.newInstance(targetClazz);
                holder.init(indexName, read.getRequestHooks());
                SearchSourceBuilder source = holder.getSource();
                if (ArrayUtils.isNotEmpty(this.includeFields) || ArrayUtils.isNotEmpty(this.excludeFields)) {
                    source.fetchSource(this.includeFields, this.excludeFields);
                }
                return (R) holder;
            }
            throw new RuntimeException("un-support this query model");
        }
    }

}
