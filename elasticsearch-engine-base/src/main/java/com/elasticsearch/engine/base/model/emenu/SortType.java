package com.elasticsearch.engine.base.model.emenu;

/**
 * @author wanghuan
 * @description: 分组排序类型
 * @date 2022-01-26 11:28
 */
public enum SortType {

    /**
     * 按照分组的key 正序排序
     */
    KEY_ASC("KEY_ASC"),
    /**
     * 按照分组的key 倒序排序
     */
    KEY_DESC("KEY_DESC"),
    /**
     * 按照分组的count 正序排序
     */
    COUNT_ASC("COUNT_ASC"),
    /**
     * 按照分组的count 倒序排序
     */
    COUNT_DESC("COUNT_DESC");

    private String name;

    SortType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }


}
