package com.elasticsearch.engine.model.emenu;

/**
 * 描述 ES 涉及的查询逻辑连接符
 *
 * @author wanghuan
 * @date 2022-01-26 11:28
 */
public enum EsConnector {

	/**
	 * ---- bool query
	 */
	MUST,
	MUST_NOT,
	FILTER,
	/**
	 * 不会影响过滤， 只影响文档评分
	 */
	SHOULD

}
