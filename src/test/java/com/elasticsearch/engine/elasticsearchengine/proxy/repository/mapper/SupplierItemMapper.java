package com.elasticsearch.engine.elasticsearchengine.proxy.repository.mapper;

import com.elasticsearch.engine.elasticsearchengine.execute.querymodel.SupplierItem;
import com.elasticsearch.engine.elasticsearchengine.execute.resultmodel.SupplierItemEntity;
import com.elasticsearch.engine.elasticsearchengine.mapping.annotation.hook.UseRequestHook;
import com.elasticsearch.engine.elasticsearchengine.mapping.annotation.hook.UseResponseHook;
import com.elasticsearch.engine.elasticsearchengine.model.annotion.EsHelperProxy;
import com.elasticsearch.engine.elasticsearchengine.model.domain.BaseResp;
import com.elasticsearch.engine.elasticsearchengine.proxy.repository.entity.result.AccountAggResult;

/**
 * EsAccountMapper
 *
 * @author JohenTeng
 * @date 2021/12/9
 */
@EsHelperProxy
public interface SupplierItemMapper {

	/**
	 * @param param simple es query
	 */
	BaseResp<SupplierItemEntity> queryByParam(SupplierItem param);


	@UseRequestHook("aggReqHook")
	@UseResponseHook("aggResRespHook")
	AccountAggResult aggByParamAnn(SupplierItem param);

}
