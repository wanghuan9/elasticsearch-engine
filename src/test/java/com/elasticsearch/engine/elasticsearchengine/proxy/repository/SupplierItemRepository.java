package com.elasticsearch.engine.elasticsearchengine.proxy.repository;

import com.elasticsearch.engine.elasticsearchengine.execute.querymodel.SupplierItem;
import com.elasticsearch.engine.elasticsearchengine.execute.querymodel.SupplierItemResExtend;
import com.elasticsearch.engine.elasticsearchengine.execute.resultmodel.AggEntityExtend;
import com.elasticsearch.engine.elasticsearchengine.execute.resultmodel.SupplierItemEntity;
import com.elasticsearch.engine.elasticsearchengine.model.domain.BaseESRepository;
import com.elasticsearch.engine.elasticsearchengine.model.domain.BaseResp;
import com.elasticsearch.engine.elasticsearchengine.proxy.entity.params.SupplierItemProxyResExtend;
import org.elasticsearch.action.search.SearchResponse;

import java.util.List;

/**
 * EsAccountMapper
 * response:
 * 1.T *
 * 2.自定义 *
 *
 * @author JohenTeng
 * @date 2021/12/9
 */
public interface SupplierItemRepository extends BaseESRepository<SupplierItemEntity, String> {

    /**
     * 查询单个
     *
     * @param param
     * @return
     */
    SupplierItemEntity queryOne(SupplierItem param);

    /**
     * List查询
     *
     * @param param
     * @return
     */
    List<SupplierItemEntity> queryList(SupplierItem param);

    /**
     * List查询
     * BaseResp 用于拿到list及count
     *
     * @param param
     * @return
     */
    BaseResp<SupplierItemEntity> queryByParam(SupplierItem param);

    /**
     * 自定义结果查询(分组查询)
     *
     * @param param
     * @return
     */
    List<AggEntityExtend> queryAggs(SupplierItemResExtend param);

    /**
     * 辅助查询
     *
     * @return
     */
    SearchResponse querySearchResponse(SupplierItemProxyResExtend param);


//    @UseRequestHook("aggReqHook")
//    @UseResponseHook("aggResRespHook")
//    AccountAggResult aggByParamAnn(SupplierItem param);

}
