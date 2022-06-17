package com.elasticsearch.engine.base.common.parse.ann.param;


import com.elasticsearch.engine.base.common.utils.CaseFormatUtils;
import com.elasticsearch.engine.base.common.utils.ReflectionUtils;
import com.elasticsearch.engine.base.config.EsEngineConfig;
import com.elasticsearch.engine.base.mapping.annotation.Term;
import com.elasticsearch.engine.base.mapping.annotation.Terms;
import com.elasticsearch.engine.base.model.annotion.Base;
import com.elasticsearch.engine.base.model.annotion.EsQueryIndex;
import com.elasticsearch.engine.base.model.annotion.Query;
import com.elasticsearch.engine.base.model.constant.CommonConstant;
import com.elasticsearch.engine.base.model.domain.*;
import com.elasticsearch.engine.base.model.emenu.EsConnector;
import com.elasticsearch.engine.base.model.emenu.QueryModel;
import com.elasticsearch.engine.base.model.exception.EsEngineConfigException;
import com.elasticsearch.engine.base.model.exception.EsEngineQueryException;
import com.google.common.collect.Lists;
import joptsimple.internal.Strings;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.text.WordUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author wanghuan
 * @description: QueryAnnParser
 * @date 2022-01-26 11:28
 */
public class QueryParamAnnParser {
    private static final String BASE_FILED = "value";

    private volatile static QueryParamAnnParser INSTANCE;

    private QueryParamAnnParser() {
    }

    /**
     * 创建单例的QueryAnnParser
     *
     * @return
     */
    public static QueryParamAnnParser instance() {
        if (INSTANCE == null) {
            synchronized (QueryParamAnnParser.class) {
                if (INSTANCE == null) {
                    INSTANCE = new QueryParamAnnParser();
                    return INSTANCE;
                }
            }
        }
        return INSTANCE;
    }

    /**
     * 通过方法解析查询索引的信息
     *
     * @param method
     * @return
     */
    public EsQueryIndexBean getIndex(Method method) {
        Class<?> clazz = method.getDeclaringClass();
        EsQueryIndex ann = clazz.getAnnotation(EsQueryIndex.class);
        if (ann == null) {
            throw new EsEngineQueryException("undefine query-index @EsQueryIndex");
        }
        String index = ann.value();
        QueryModel model = ann.model();
        String[] includeFields = ann.include();
        String[] excludeFields = ann.exclude();
        return new EsQueryIndexBean(index, model, includeFields, excludeFields);
    }

    /**
     * 解析参数
     *
     * @param args
     * @return
     */
    public ParamParserResultModel read(Method method, Object[] args) {
        ParamParserResultModel parserResultModel = new ParamParserResultModel();
        List<EsQueryFieldBean> queryDesList = parserArgs(method, args, parserResultModel.getRequestHooks());
        //按字段解析顺序排序
        List<EsQueryFieldBean> queryDesListOrder = queryDesList.stream().sorted(Comparator.comparing(EsQueryFieldBean::getOrder)).collect(Collectors.toList());
        parserResultModel.setQueryDesList(queryDesListOrder);
        return parserResultModel;
    }

    /**
     * 解析参数
     *
     * @param method
     * @param args
     * @param requestHooks
     * @return
     */
    private List<EsQueryFieldBean> parserArgs(Method method, Object[] args, List<Object> requestHooks) {

        List<EsQueryFieldBean> queryDesList = Lists.newArrayList();
        // 获取方法的所有参数
        Parameter[] parameters = method.getParameters();
        for (int i = 0; i < parameters.length; i++) {
            mapField(queryDesList, parameters[i], args[i]);
        }
        return queryDesList;
    }


    /**
     * 解析具体的参数  包括带有注解属性和 不带注解属性的默认解析
     *
     * @param queryDesList
     * @param param
     * @param paramValue
     */
    private void mapField(List<EsQueryFieldBean> queryDesList, Parameter param, Object paramValue) {
        Set<Annotation> annotationSet = Arrays.stream(param.getAnnotations())
                .filter(ann -> ann.annotationType().isAnnotationPresent(Query.class))
                .collect(Collectors.toSet());
        if (CollectionUtils.isNotEmpty(annotationSet)) {
            List<EsQueryFieldBean> queryDes = this.mapFieldAnn(param, paramValue, annotationSet);
            queryDesList.addAll(queryDes);
        } else {
            //默认的全局忽略字段  不加term 查询
            //例如 log字段,当类添加了 @Slf4j 默认会加上log的本地变量  pageSize
            if (EsEngineConfig.getQueryIgnoreParam().contains(param.getName())) {
                return;
            }
            //没有添加注解的 基础类型设置默认 trem/trems查询
            if (EsEngineConfig.isIsBuildDefault()) {
                List<EsQueryFieldBean> queryDes = this.mapFieldAnnDefault(param, paramValue);
                queryDesList.addAll(queryDes);
            }
        }
    }

    /**
     * 解析带注解的参数
     *
     * @param param
     * @param paramValue
     * @param annotationSet
     * @return
     */
    private List<EsQueryFieldBean> mapFieldAnn(Parameter param, Object paramValue, Set<Annotation> annotationSet) {
        final List<EsQueryFieldBean> res = Lists.newArrayList();
        for (Annotation ann : annotationSet) {
            //TODO 代码优化
            //检查参数类型
            fieldTypeCheck(param, paramValue);
            //扩展 不需要value值的参数
            Optional.ofNullable(parseValue(paramValue)).ifPresent(queryDes -> {
                this.parseAnn(queryDes, param, ann);
                res.add(queryDes);
            });
        }
        return res;
    }

    /**
     * 校验参数类型
     *
     * @param param
     * @param paramValue
     */
    private void fieldTypeCheck(Parameter param, Object paramValue) {
        Class<?> paramType = param.getType();
        if (Objects.isNull(paramValue)) {
            return;
        }
        //支持基础类型/List/扩展类型
        if (ReflectionUtils.isBaseTypeAndExtend(paramType) || ReflectionUtils.checkCollectionValueType(param, paramValue) || paramValue instanceof EsComplexParam) {
            //TODO 代码优化
            //TODO 类型转换器扩展 支持
            //TODO 参数校验器扩展***
        } else {
            throw new EsEngineQueryException("es annotation query parameter has wrong parameter, just support primitive type or their decorate type is list or EsComplexParam ; error param is: " + param.getName());
        }
    }

    /**
     * 解析不带注解的参数
     *
     * @param param
     * @param paramValue
     * @return
     */
    private List<EsQueryFieldBean> mapFieldAnnDefault(Parameter param, Object paramValue) {
        final List<EsQueryFieldBean> res = Lists.newArrayList();
        Optional.ofNullable(parseValueDefault(param, paramValue)).ifPresent(queryDes -> {
            this.parseAnnDefault(queryDes, param, paramValue);
            res.add(queryDes);
        });
        return res;
    }


    /**
     * 解析参数的 value
     *
     * @param val
     * @return
     */
    private EsQueryFieldBean parseValue(Object val) {
        EsQueryFieldBean queryDes = new EsQueryFieldBean<>();
        if (Objects.isNull(val)) {
            return null;
        }
        //TODO 代码优化
        //TODO 类型转换器扩展 支持
        //TODO 参数校验器扩展***
        //先临时加强对 Collection 和 String的校验 后续扩展参数校验器
        if (checkListAndString(val)) {
            return null;
        }
        queryDes.setValue(val);
        return queryDes;
    }

    /**
     * 解析默认的没有添加注解的param 的 value
     *
     * @param param
     * @param paramValue
     * @return
     */
    private EsQueryFieldBean parseValueDefault(Parameter param, Object paramValue) {
        EsQueryFieldBean queryDes = new EsQueryFieldBean<>();
        Class<?> fieldType = param.getType();
        if (Objects.isNull(paramValue)) {
            return null;
        }
        //支持基础类型/List
        if (ReflectionUtils.isBaseTypeAndExtend(fieldType) || ReflectionUtils.checkCollectionValueType(param, paramValue)) {
            //先临时加强对 Collection 和 String的校验 后续扩展参数校验器
            if (checkListAndString(paramValue)) {
                return null;
            }
            queryDes.setValue(paramValue);
        } else {
            throw new EsEngineQueryException("es annotation query field has error field, just support primitive type or their decorate type is List ; error param is: " + param.getName());
        }
        return queryDes;
    }

    /**
     * 加强 Collection 和 String的校验
     *
     * @param val
     * @return
     */
    private Boolean checkListAndString(Object val) {
        if (val instanceof List) {
            //移除list中 为nll,或者为空串的元素
            List listParam = (List) val;
            for (int i = 0; i < listParam.size(); i++) {
                Object obj = listParam.get(i);
                if (Objects.isNull(obj) || StringUtils.isEmpty(obj.toString().replaceAll(CommonConstant.SPECIAL_CHAR, ""))) {
                    listParam.remove(i);
                }
            }
            if (CollectionUtils.isEmpty(listParam)) {
                return true;
            }
        }
        if (val instanceof String) {
            String strVal = (String) val;
            strVal = strVal.replaceAll(CommonConstant.SPECIAL_CHAR, "");
            return StringUtils.isEmpty(strVal);
        }

        return false;
    }


    /**
     * 解析参数的 注解
     *
     * @param queryDes
     * @param param
     * @param targetAnn
     */
    private void parseAnn(EsQueryFieldBean queryDes, Parameter param, Annotation targetAnn) {
        try {
            queryDes.setExtAnnotation(targetAnn);
            Method baseMethod = targetAnn.getClass().getDeclaredMethod(BASE_FILED);
            Base ann = (Base) baseMethod.invoke(targetAnn);
            EsConnector esConnector = ann.connect();
            if (esConnector == null) {
                throw new EsEngineQueryException("ES-QUERY-LOGIC-CONNECTOR cant be null");
            }
            queryDes.setLogicConnector(esConnector);

            String column = ann.value();
            if (StringUtils.isBlank(column)) {
                column = param.getName();
                //TODO 优化
                //设置list后缀
                for (String fix : EsEngineConfig.getQueryParamPrefixAndSuffix()) {
                    if (column.contains(fix)) {
                        column = column.replace(fix, "");
                        break;
                    }
                }
                //若设置默认下划线
                if (EsEngineConfig.isNamingStrategy()) {
                    column = CaseFormatUtils.camelToUnderscore(column);
                }
            }
            queryDes.setField(column);
            queryDes.setOrder(ann.order());
            String query = targetAnn.annotationType().getSimpleName();
            if (StringUtils.isBlank(query)) {
                throw new EsEngineQueryException("QUERY-TYPE missing, it's necessary");
            }
            queryDes.setQueryType(query);

        } catch (Exception e) {
            throw new EsEngineConfigException("annotation analysis Error, cause:", e);
        }
    }

    /**
     * 解析默认的没有添加注解的param 的 注解
     *
     * @param queryDes
     * @param param
     * @param paramValue
     */
    private void parseAnnDefault(EsQueryFieldBean queryDes, Parameter param, Object paramValue) {
        try {
            EsConnector esConnector = EsConnector.FILTER;
            queryDes.setLogicConnector(esConnector);
            String column = param.getName();
            //设置list后缀
            for (String fix : EsEngineConfig.getQueryParamPrefixAndSuffix()) {
                if (column.contains(fix)) {
                    column = column.replace(fix, "");
                    break;
                }
            }
            //若设置默认下划线
            if (EsEngineConfig.isNamingStrategy()) {
                column = CaseFormatUtils.camelToUnderscore(column);
            }
            queryDes.setField(column);
            Class<?> fieldType = param.getType();
            String query = Strings.EMPTY;
            if (ReflectionUtils.isBaseTypeAndExtend(fieldType)) {
                query = Term.class.getSimpleName();
                String queryField = WordUtils.uncapitalize(query);
                Term annotation = DefaultQueryModel.class.getField(queryField).getAnnotation(Term.class);
                queryDes.setExtAnnotation(annotation);
            } else if (ReflectionUtils.checkCollectionValueType(param, paramValue)) {
                query = Terms.class.getSimpleName();
                String queryField = WordUtils.uncapitalize(query);
                Terms annotation = DefaultQueryModel.class.getField(queryField).getAnnotation(Terms.class);
                queryDes.setExtAnnotation(annotation);
            }
            if (StringUtils.isBlank(query)) {
                throw new EsEngineQueryException("QUERY-TYPE missing, it's necessary");
            }
            queryDes.setQueryType(query);
        } catch (Exception e) {
            throw new EsEngineConfigException("annotation analysis Error, cause:", e);
        }
    }

}
