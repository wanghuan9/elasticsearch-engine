package com.elasticsearch.engine.elasticsearchengine.common.factory;

/**
 * @Author 王欢
 * @Date 2020/02/19
 * @Time 15:28:59
 */
public interface MatchingBean<T> {
    Boolean matching(T factory);
}
