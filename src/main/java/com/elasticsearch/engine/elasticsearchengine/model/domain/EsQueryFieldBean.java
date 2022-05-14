package com.elasticsearch.engine.elasticsearchengine.model.domain;

import com.elasticsearch.engine.elasticsearchengine.model.emenu.EsConnector;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.lang.annotation.Annotation;

/**
 * @author wanghuan
 * @description: EsQueryFieldBean
 * @date 2022-01-26 11:28
 */
@Slf4j
@Data
public class EsQueryFieldBean<T extends AbstractQueryBean> implements Serializable {

	/**
	 * the field be queried
	 */
	private String field;

	/**
	 * the val of query for
	 */
	private Object value;

	/**
	 * query Type like <h>Match,Fuzzy,Term ...</h>
	 */
	private String queryType;

	/**
	 * connector of multi-query (must, should, must not, filter)
	 */
	private EsConnector logicConnector;

	/**
	 * extend query bean
	 */
	private T extBean;

	/**
	 * annotation that annotated by @Query
	 */
	private Annotation extAnnotation;

	/**
	 * 字段解析顺序
	 */
	private Integer order = 0;
}
