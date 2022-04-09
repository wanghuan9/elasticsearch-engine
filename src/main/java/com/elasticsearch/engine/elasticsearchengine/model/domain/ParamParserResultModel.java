package com.elasticsearch.engine.elasticsearchengine.model.domain;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * @author wanghuan
 * @description: 查询对象解析结果
 * @date 2022-04-02 08:06
 */
@Data
public class ParamParserResultModel {

    /**
     * 查询注解解析
     */
    List<EsQueryFieldBean> queryDesList;

    /**
     * 自定义嵌套查询扩展解析
     */
    List<Object> requestHooks = new ArrayList<>();


}
