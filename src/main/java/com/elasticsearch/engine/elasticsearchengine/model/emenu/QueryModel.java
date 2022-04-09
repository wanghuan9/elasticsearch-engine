package com.elasticsearch.engine.elasticsearchengine.model.emenu;

/**
 * query 最外层查询结构 默认BOOL
 * <p>
 * {
 * "query": {
 * "bool": {},
 * "exists": {}
 * }
 * }
 *
 * @author wanghuan
 * @date 2022-01-26 11:28
 */
public enum QueryModel {

	/**
	 * bool 组合查询
	 */
	BOOL,
	DIS_MAX,
	COMMON,
	EXISTS,

}
