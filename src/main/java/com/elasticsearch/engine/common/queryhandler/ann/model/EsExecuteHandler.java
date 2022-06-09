package com.elasticsearch.engine.common.queryhandler.ann.model;

import com.elasticsearch.engine.GlobalConfig;
import com.elasticsearch.engine.common.parse.ann.model.EsQueryEngine;
import com.elasticsearch.engine.holder.AbstractEsRequestHolder;
import com.elasticsearch.engine.model.domain.BaseResp;
import com.elasticsearch.engine.model.exception.EsEngineQueryException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.List;

/**
 * @author wanghuan
 * @description: es 搜索工具类
 * @date 2021-09-29
 * @time 10:33
 */
@Slf4j
@Component
public class EsExecuteHandler extends EsBaseExecuteHandle {

//    /**
//     * 分页查询
//     *
//     * @param param         需要解析的查询实体
//     * @param responseClazz 查询结果实体类型
//     * @param <T>           查询结果实体类型对应的泛型
//     * @param <U>           请求参数对应的泛型
//     * @return
//     */
//    public <T, U extends PageRequest> BaseResp<T> executePage(U param, Class<T> responseClazz) {
//        AbstractEsRequestHolder esHolder = EsQueryEngine.execute(param, Boolean.TRUE);
//        return baseExecute(param, responseClazz, esHolder);
//    }

    /**
     * 分页查询扩展结果构建
     *
     * @param param           需要解析的查询实体
     * @param K               es查询结果实体类型
     * @param convertFunction 查询结果转换函数
     * @param <K>             es查询结果实体类型对应的泛型
     * @param <T>             转换函数结果实体类型对应的泛型
     * @param <U>             请求参数对应的泛型
     * @return
     */
//    public <K, T, U extends PageRequest> RestPageDataResponse<T> executePage(U param, Class<K> K, Function<List<K>,List<T>> convertFunction) {
//        AbstractEsRequestHolder esHolder = EsQueryEngine.execute(param, Boolean.TRUE);
//        BaseResp<K> res = baseExecute(param, K, esHolder);
//        return Page.toPage(param, res::getTotalHit, () -> convertFunction.apply(res.getRecords())).toResponse();
//    }


    /**
     * List查询并构建结果
     *
     * @param method        被代理的方法
     * @param param         需要解析的查询实体
     * @param responseClazz 查询结果实体类型
     * @param <T>           查询结果实体类型对应的泛型
     * @return
     */
    public <T> List<T> executeList(Method method, Object param, Class<T> responseClazz) {
        BaseResp<T> result = execute(method, param, responseClazz);
        return result.getRecords();
    }

    /**
     * 单个查询并构建结果
     * 若查询结果>1 会抛异常
     *
     * @param method        被代理的方法
     * @param param         需要解析的查询实体
     * @param responseClazz 查询结果实体类型
     * @param <T>           查询结果实体类型对应的泛型
     * @return
     */
    public <T> T executeOne(Method method, Object param, Class<T> responseClazz) {
        BaseResp<T> result = execute(method, param, responseClazz);
        if (CollectionUtils.isEmpty(result.getRecords())) {
            return null;
        }
        if (result.getRecords().size() > 1) {
            throw new EsEngineQueryException("except one result, but find more");
        }
        return result.getRecords().stream().findFirst().get();
    }

    /**
     * 代理查询并构建结果(自定义扩展查询-兼容invoke)
     *
     * @param method        被代理的方法
     * @param param         需要解析的查询实体
     * @param responseClazz 查询结果实体类型
     * @param <T>           查询结果实体类型对应的泛型
     * @return
     */
    public <T> BaseResp<T> execute(Method method, Object param, Class responseClazz) {
        AbstractEsRequestHolder esHolder = EsQueryEngine.execute(method, param, GlobalConfig.visitQueryBeanParent);
        return baseExecute(param, responseClazz, esHolder);
    }

    /**
     * 固定结果类型查询
     *
     * @param method 被代理的方法
     * @param param  需要解析的查询实体
     * @return
     */
    public BaseResp executeDefaultResp(Method method, Object param) {
        //方法返回值
        Class<?> returnType = method.getReturnType();
        String simpleName = "a";
        switch (simpleName) {
            case "a":
                return executeAggs(method, param);
        }
        throw new EsEngineQueryException("");
    }

    /**
     * 单个查询并构建结果
     * 若查询结果>1 会抛异常
     *
     * @param param         需要解析的查询实体
     * @param responseClazz 查询结果实体类型
     * @param <T>           查询结果实体类型对应的泛型
     * @return
     */
    public <T> T executeOne(Object param, Class<T> responseClazz) {
        return executeOne(null, param, responseClazz);
    }


    /**
     * List查询并构建结果
     *
     * @param param         需要解析的查询实体
     * @param responseClazz 查询结果实体类型
     * @param <T>           查询结果实体类型对应的泛型
     * @return
     */
    public <T> List<T> executeList(Object param, Class<T> responseClazz) {
        return executeList(null, param, responseClazz);
    }

    /**
     * 代理查询并构建结果(自定义扩展查询-兼容invoke)
     *
     * @param param         需要解析的查询实体
     * @param responseClazz 查询结果实体类型
     * @param <T>           查询结果实体类型对应的泛型
     * @return
     */
    public <T> BaseResp<T> execute(Object param, Class responseClazz) {
        return execute(null, param, responseClazz);
    }


    /**
     * 获取构建的查询条件
     *
     * @param param 需要解析的查询实体
     * @return
     */
    public SearchSourceBuilder getSearchSourceBuilder(Object param) {
        AbstractEsRequestHolder esHolder = EsQueryEngine.execute(null, param, GlobalConfig.visitQueryBeanParent);
        return esHolder.getSource();
    }

}
