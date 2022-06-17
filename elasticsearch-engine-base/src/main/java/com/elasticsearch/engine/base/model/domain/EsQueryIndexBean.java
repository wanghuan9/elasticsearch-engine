package com.elasticsearch.engine.base.model.domain;


import com.elasticsearch.engine.base.model.emenu.QueryModel;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author wanghuan
 * @description: EsQueryIndexBean
 * @date 2022-01-26 11:28
 */
@Data
@AllArgsConstructor
public class EsQueryIndexBean {

	/**
	 * 索引名称
	 */
	private String indexName;

	/**
	 * query 最外层结构体
	 */
	private QueryModel esQueryModel;

	/**
	 * 包含的返回字段
	 */
	private String[] includeFields;

	/**
	 * 排除的返回字段
	 */
	private String[] excludeFields;

}
