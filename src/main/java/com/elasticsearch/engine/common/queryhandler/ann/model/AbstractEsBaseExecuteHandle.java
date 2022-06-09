package com.elasticsearch.engine.common.queryhandler.ann.model;

import com.elasticsearch.engine.common.utils.ReflectionUtils;
import com.elasticsearch.engine.holder.AbstractEsRequestHolder;
import com.elasticsearch.engine.hook.RequestHook;
import com.elasticsearch.engine.hook.ResponseHook;
import com.elasticsearch.engine.model.domain.BaseResp;
import com.elasticsearch.engine.model.exception.EsEngineConfigException;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.elasticsearch.action.search.SearchResponse;
import org.springframework.core.env.Environment;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author wanghuan
 * @description: ROOD
 * @date 2022-05-06 18:30
 */
@Resource
public abstract class AbstractEsBaseExecuteHandle {

    @Resource
    private Environment environment;

    /**
     * 前置处理扩展
     *
     * @param param
     * @param esHolder
     */
    public abstract void executePostProcessorBefore(Object param, AbstractEsRequestHolder esHolder);

    /**
     * 后置处理扩展 嵌套扩展处理
     */
    public abstract <T> void executePostProcessorAfter(Object param, SearchResponse resp, BaseResp<T> result);

    /**
     * 索引名动态配置解析
     *
     * @param esHolder
     */
    protected void resetIndexName(AbstractEsRequestHolder esHolder) {
        String[] indices = esHolder.getRequest().indices();
        if (Objects.isNull(indices) || indices.length != NumberUtils.INTEGER_ONE) {

        }
        String indexName = indices[0];
        if (StringUtils.contains(indexName, "${") && StringUtils.contains(indexName, "}")) {
            String parseIndexName = indexName.replace("${", "").replace("}", "");
            String envProperty = parseIndexName;
            //包含默认值
            if (StringUtils.contains(parseIndexName, ":")) {
                String[] index = StringUtils.split(parseIndexName, ":");
                envProperty = index[0];
                indexName = index[1];
            }
            //读取配置的索引名
            String configIndex = environment.getProperty(envProperty);
            //配置的是读取配置中心的参数, 没有读到,并且没有设置默认值, 则抛出异常
            if (StringUtils.isEmpty(configIndex) && !parseIndexName.contains(":")) {
                throw new EsEngineConfigException("index name is empty");
            }
            //优先使用配置中心配置的索引名
            if (StringUtils.isNotEmpty(configIndex)) {
                indexName = configIndex;
            }
        }
        esHolder.getRequest().indices(indexName);
    }

    /**
     * 自定义扩展查询处理
     *
     * @param param
     * @return
     */
    protected List<RequestHook> checkRequestHook(Object param) {
        List<RequestHook> requestHooks = new ArrayList<>();
        List<Class<?>> superClass = ReflectionUtils.getSuperClass(param.getClass());
        superClass.add(param.getClass());
        for (Class<?> clazz : superClass) {
            if (RequestHook.class.isAssignableFrom(clazz)) {
                try {
                    requestHooks.add((RequestHook) clazz.newInstance());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return requestHooks;
    }

    /**
     * 自定义结果解析扩展处理
     *
     * @param param
     * @return
     */
    protected ResponseHook checkResponseHook(Object param) {
        if (ResponseHook.class.isAssignableFrom(param.getClass())) {
            return (ResponseHook) param;
        }
        return null;
    }
}
