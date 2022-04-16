package com.elasticsearch.engine.elasticsearchengine.proxy.entity.params;

import com.elasticsearch.engine.elasticsearchengine.hook.ResponseHook;
import com.elasticsearch.engine.elasticsearchengine.model.annotion.EsQueryIndex;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.search.SearchResponse;

import java.util.List;

@Slf4j
@EsQueryIndex(index = "supplier_item_spare")
@Data
public class SupplierItemProxyResExtend implements ResponseHook<SearchResponse> {

    private List<String> itemNoList;

    /**
     * user define the method to handle ElasticSearch-Response
     *
     * @param resp return
     */
    @Override
    public SearchResponse handleResponse(SearchResponse resp) {
        return resp;
    }
}

