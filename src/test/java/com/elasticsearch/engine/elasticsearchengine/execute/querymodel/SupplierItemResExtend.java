package com.elasticsearch.engine.elasticsearchengine.execute.querymodel;

import com.elasticsearch.engine.elasticsearchengine.execute.resultmodel.AggEntityExtend;
import com.elasticsearch.engine.elasticsearchengine.hook.ResponseHook;
import com.elasticsearch.engine.elasticsearchengine.mapping.annotation.Aggs;
import com.elasticsearch.engine.elasticsearchengine.model.annotion.EsQueryIndex;
import com.elasticsearch.engine.elasticsearchengine.model.annotion.Ignore;
import com.elasticsearch.engine.elasticsearchengine.model.constant.EsConstant;
import com.elasticsearch.engine.elasticsearchengine.model.exception.EsHelperQueryException;
import com.google.common.collect.Lists;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;

import java.util.List;
import java.util.Objects;

@Slf4j
@EsQueryIndex(index = "supplier_item_spare")
@Data
public class SupplierItemResExtend implements ResponseHook<List<AggEntityExtend>> {

    @Aggs
    private Integer status;

    @Ignore
    private List<String> itemNoList;

    /**
     * user define the method to handle ElasticSearch-Response
     *
     * @param resp return
     */
    @Override
    public List<AggEntityExtend> handleResponse(SearchResponse resp) {
        List<AggEntityExtend> rr = Lists.newArrayList();
        if (Objects.isNull(resp.getAggregations())) {
            throw new EsHelperQueryException("aggs param is null, result aggs is null");
        }
//        resp.getInternalResponse()
        Terms aggs = resp.getAggregations().get(EsConstant._AGG);
        for (Terms.Bucket agg : aggs.getBuckets()) {
            if (!agg.getKeyAsString().equals(EsConstant.STRING_ZERO)) {
                AggEntityExtend result = new AggEntityExtend();
                Integer id = Integer.valueOf(agg.getKeyAsString());
                long docCount = agg.getDocCount();
                result.setKey(id);
                result.setCount(docCount);
                rr.add(result);
            }
        }
        return rr;
    }
}

