package com.elasticsearch.engine.common.factory;

/**
 * @Author 王欢
 * @Date 2020/02/19
 * @Time 15:28:59
 */
public interface FactoryList<E extends MatchingBean<K>, K> {
    E getBean(K factory);
}
