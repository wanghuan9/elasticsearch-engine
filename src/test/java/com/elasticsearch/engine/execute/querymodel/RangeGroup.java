package com.elasticsearch.engine.execute.querymodel;

import com.elasticsearch.engine.mapping.annotation.From;
import com.elasticsearch.engine.mapping.annotation.To;
import com.elasticsearch.engine.model.annotion.EsQueryIndex;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author wanghuan
 * @description: range 分组测试
 * @date 2022-04-08 23:36
 */
@EsQueryIndex(value = "supplier_item_spare")
@Data
public class RangeGroup {

    @From(group = 1)
    private LocalDateTime createDtStart;

    @From
    private Integer startStatus;

    @To(group = 1)
    private LocalDateTime endCreateDt;

    @To
    private Integer endStatus;
}
