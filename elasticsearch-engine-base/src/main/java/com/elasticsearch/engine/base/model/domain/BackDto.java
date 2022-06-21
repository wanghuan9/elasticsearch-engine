package com.elasticsearch.engine.base.model.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author wanghuan
 * @description BackDto
 * @mail 958721894@qq.com
 * @date 2022-06-05 15:44
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class BackDto {

    private String backColumn;
    private Class<?> backColumnTyp;
   
}
