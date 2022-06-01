package com.elasticsearch.engine.mapping.handler;

import com.elasticsearch.engine.common.utils.ExtAnnBeanMapUtils;
import com.elasticsearch.engine.holder.AbstractEsRequestHolder;
import com.elasticsearch.engine.model.domain.EsQueryFieldBean;
import com.elasticsearch.engine.model.domain.AbstractQueryBean;
import com.elasticsearch.engine.model.exception.EsHelperConfigException;
import org.elasticsearch.index.query.QueryBuilder;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Objects;

/**
 * @author wanghuan
 * @description: AbstractQueryHandler
 * @date 2021-09-29
 * @time 10:33
 */
public abstract class AbstractQueryHandler<T extends AbstractQueryBean> {

    /**
     * 查询注解解析拼接查询语句
     *
     * @param queryDes
     * @param searchHelper return
     */
    public abstract QueryBuilder handle(EsQueryFieldBean<T> queryDes, AbstractEsRequestHolder searchHelper);

    /**
     * execute param-explain
     *
     * @param queryDes
     * @param searchHelper return
     */
    public final AbstractEsRequestHolder execute(EsQueryFieldBean<T> queryDes, AbstractEsRequestHolder searchHelper) {
        //拼接查询连接
        searchHelper.changeLogicConnector(queryDes.getLogicConnector());
        //构建扩展查询类
        handleExtBean(queryDes);
        //拼接查询语句
        QueryBuilder queryBuilder = handle(queryDes, searchHelper);
        if (Objects.nonNull(queryBuilder)) {
            queryDes.getExtBean().configQueryBuilder(queryBuilder);
            handleExtConfig(queryDes, queryBuilder);
        }
        return searchHelper;
    }

    /**
     * explain the extend-params
     * final handle-process of query-field-reader
     * TODO 扩展点
     *
     * @param queryDes return
     */
    protected void handleExtConfig(EsQueryFieldBean<T> queryDes, QueryBuilder queryBuilder) {
        // do nothing, if need translate QueryDes.extendDefine, you need implement this method
    }

    /**
     * mapping the annotation
     *
     * @param queryDes return
     */
    protected final EsQueryFieldBean<T> handleExtBean(EsQueryFieldBean<T> queryDes) {
        try {
            Type[] actualTypeArguments = ((ParameterizedType) this.getClass().getGenericSuperclass()).getActualTypeArguments();
            if (actualTypeArguments.length == 0) {
                return queryDes;
            }
            String fullClassPath = actualTypeArguments[0].getTypeName();
            Class<?> aClass = Class.forName(fullClassPath);
            T extBean = (T) ExtAnnBeanMapUtils.mapping(queryDes.getExtAnnotation(), aClass);
            queryDes.setExtBean(extBean);
            return queryDes;
        } catch (ClassNotFoundException e) {
            throw new EsHelperConfigException("queryHandle-actualType class not found, cause:", e);
        }
    }

}
