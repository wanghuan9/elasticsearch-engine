package com.elasticsearch.engine.elasticsearchengine.model.domain;

import lombok.Data;

/**
 * @author wanghuan
 * @description: sql查询异常响应
 * @date 2022-05-13 22:24
 */
@Data
public class SqlErrorResponse {

    /**
     * 错误信息
     */
    private SqlErrorResponse.ErrorDTO error;
    
    /**
     * 响应状态
     */
    private Integer status;

    
    @Data
    public static class ErrorDTO{
        String type;
        String reason;
    }
}
