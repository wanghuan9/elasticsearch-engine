package com.elasticsearch.engine.proxy.entity.result;

import java.util.Map;

/**
 * AccountAggResult
 *
 * @author JohenTeng
 * @date 2021/12/9
 */
public class AccountAggResult {

    private Map<String, Long> data;

    public Map<String, Long> getData() {
        return data;
    }

    public void setData(Map<String, Long> data) {
        this.data = data;
    }
}
