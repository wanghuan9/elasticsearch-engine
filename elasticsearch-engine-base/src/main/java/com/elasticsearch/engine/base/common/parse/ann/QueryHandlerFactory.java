package com.elasticsearch.engine.base.common.parse.ann;

import com.elasticsearch.engine.base.common.utils.ReflectionUtils;
import com.elasticsearch.engine.base.mapping.handler.AbstractQueryHandler;
import com.elasticsearch.engine.base.model.annotion.EsQueryHandle;
import com.elasticsearch.engine.base.model.exception.EsEngineConfigException;
import com.elasticsearch.engine.base.model.exception.EsEngineQueryException;
import org.apache.commons.lang3.StringUtils;
import org.reflections.Reflections;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author wanghuan
 * @description: QueryHandlerFactory
 * @date 2022-01-26 11:28
 */
public class QueryHandlerFactory {

    public static final Map<String, AbstractQueryHandler> QUERY_HANDLE_MAP = new HashMap<>();

    /**
     * 初始化查询注解和对应的Handler映射
     * key: Term value:TermQueryHandler.class
     */
    static {
        //获取该路径下所有类
        Reflections reflections = new Reflections(AbstractQueryHandler.class.getPackage().getName());
        //获取继承了AbstractQueryHandler的所有类
        Set<Class<? extends AbstractQueryHandler>> subQueryClazz = reflections.getSubTypesOf(AbstractQueryHandler.class);
        for (Class<? extends AbstractQueryHandler> targetClazz : subQueryClazz) {
            boolean flag = targetClazz.isAnnotationPresent(EsQueryHandle.class);
            if (!flag) {
                throw new EsEngineConfigException("query handle have to ann by @EsQueryHandle");
            }
            EsQueryHandle ann = targetClazz.getAnnotation(EsQueryHandle.class);
            String handleName = ann.queryType();
            if (StringUtils.isBlank(handleName)) {
                handleName = ann.value().getSimpleName();
            }
            if (StringUtils.isBlank(handleName)) {
                throw new EsEngineConfigException("handle-name is undefine");
            }
            QueryHandlerFactory.registryQueryHandler(handleName, targetClazz);
        }
    }

    /**
     * 注册查询注解和对应的Handler映射
     *
     * @param handleName
     * @param targetClazz
     */
    public static void registryQueryHandler(String handleName, Class<? extends AbstractQueryHandler> targetClazz) {
        QUERY_HANDLE_MAP.put(handleName, getTargetHandleInstance(targetClazz));
    }

    /**
     * 获取Handler实例
     *
     * @param handlerName
     * @return
     */
    public static AbstractQueryHandler getTargetHandleInstance(String handlerName) {
        AbstractQueryHandler targetHandler = QUERY_HANDLE_MAP.get(handlerName);
        if (targetHandler == null) {
            throw new EsEngineQueryException("un-match given handleName");
        }
        return targetHandler;
    }

    /**
     * 创建Handler实例
     *
     * @param targetClazz
     * @return
     */
    private static AbstractQueryHandler getTargetHandleInstance(Class<? extends AbstractQueryHandler> targetClazz) {
        return ReflectionUtils.newInstance(targetClazz);
    }


}
