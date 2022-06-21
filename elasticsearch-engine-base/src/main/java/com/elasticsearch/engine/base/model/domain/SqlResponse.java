package com.elasticsearch.engine.base.model.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

/**
* @author wanghuan
* @description SqlResponse
* @mail 958721894@qq.com       
* @date 2022/6/18 09:54 
*/
@Data
public class SqlResponse extends SqlErrorResponse {
    @JsonProperty("columns")
    private List<ColumnsDTO> columns;
    @JsonProperty("rows")
    private List<List<String>> rows;


    @Data
    public static class ColumnsDTO {
        @JsonProperty("name")
        private String name;
        @JsonProperty("type")
        private String type;
    }
}
