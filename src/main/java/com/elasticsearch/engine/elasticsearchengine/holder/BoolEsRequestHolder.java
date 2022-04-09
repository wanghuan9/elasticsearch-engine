package com.elasticsearch.engine.elasticsearchengine.holder;

import com.elasticsearch.engine.elasticsearchengine.model.emenu.EsConnector;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;

/**
 * @author wanghuan
 * @description: bool query builder holder
 * @date 2022-01-26 11:28
 */
public class BoolEsRequestHolder extends AbstractEsRequestHolder<BoolQueryBuilder> {

	/**
	 * 默认使用must连接
	 */
	@Override
	protected void defineDefaultLogicConnector() {
		super.setCurrentQueryBuilderList(super.getQueryBuilder().must());
	}

	@Override
	protected void defineQueryBuilder() {
		super.setQueryBuilder(QueryBuilders.boolQuery());
	}

	@Override
	public AbstractEsRequestHolder changeLogicConnector(EsConnector logicKey) {
		if (logicKey == null) {
			return this;
		}
		if (EsConnector.MUST.equals(logicKey)) {
			super.setCurrentQueryBuilderList(super.getQueryBuilder().must());
		}
		if (EsConnector.FILTER.equals(logicKey)) {
			super.setCurrentQueryBuilderList(super.getQueryBuilder().filter());
		}
		if (EsConnector.MUST_NOT.equals(logicKey)) {
			super.setCurrentQueryBuilderList(super.getQueryBuilder().mustNot());
		}
		if (EsConnector.SHOULD.equals(logicKey)) {
			super.setCurrentQueryBuilderList(super.getQueryBuilder().should());
		}
		return this;
	}


}
