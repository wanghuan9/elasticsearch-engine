package com.elasticsearch.engine.elasticsearchengine.model.domain;

import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * @author wanghuan
 * @description: //处理通用参数, doc_id, 深度分页id,高亮显示等
 * @date 2022-02-11 17:00
 */
@Data
public class BaseHit {

    private String docId;

    private Float hitScore;

    private Map<String, List<String>> highLightMap;
}
