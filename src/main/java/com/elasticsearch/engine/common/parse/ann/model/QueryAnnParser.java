package com.elasticsearch.engine.common.parse.ann.model;


import com.elasticsearch.engine.config.EsEngineConfig;
import com.elasticsearch.engine.common.utils.CaseFormatUtils;
import com.elasticsearch.engine.common.utils.ReflectionUtils;
import com.elasticsearch.engine.hook.RequestHook;
import com.elasticsearch.engine.mapping.annotation.Term;
import com.elasticsearch.engine.mapping.annotation.Terms;
import com.elasticsearch.engine.model.annotion.*;
import com.elasticsearch.engine.model.constant.CommonConstant;
import com.elasticsearch.engine.model.domain.*;
import com.elasticsearch.engine.model.emenu.EsConnector;
import com.elasticsearch.engine.model.emenu.QueryModel;
import com.elasticsearch.engine.model.exception.EsEngineConfigException;
import com.elasticsearch.engine.model.exception.EsEngineQueryException;
import com.google.common.collect.Lists;
import joptsimple.internal.Strings;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.text.WordUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * @author wanghuan
 * @description: QueryAnnParser
 * @date 2022-01-26 11:28
 */
public class QueryAnnParser {

    private static final String BASE_FILED = "value";

    private volatile static QueryAnnParser INSTANCE;

    private QueryAnnParser() {
    }

    /**
     * 创建单例的QueryAnnParser
     *
     * @return
     */
    public static QueryAnnParser instance() {
        if (INSTANCE == null) {
            synchronized (QueryAnnParser.class) {
                if (INSTANCE == null) {
                    INSTANCE = new QueryAnnParser();
                    return INSTANCE;
                }
            }
        }
        return INSTANCE;
    }

    public static List<Field> getFields(Class<?> clazz, boolean visitParent) {
        if (visitParent) {
            return getFields(clazz, Lists.newArrayList());
        }
        Field[] fieldArr = clazz.getDeclaredFields();
        return Lists.newArrayList(fieldArr);
    }

    public static List<Field> getFields(Class<?> clazz, List<Field> callBackList) {
        Field[] fieldArr = clazz.getDeclaredFields();
        callBackList.addAll(Arrays.asList(fieldArr));
        if (!(clazz = clazz.getSuperclass()).equals(Object.class)) {
            getFields(clazz, callBackList);
        }
        return callBackList;
    }

    /**
     * 解析入参实体类 ->EsQueryIndexBean
     *
     * @param view
     * @return
     */
    public EsQueryIndexBean getIndex(Method method, Object view) {
        //先从查询model所在的类获取, 再从method所在的类上获取
        Class<?> clazz = view.getClass();
        EsQueryIndex ann = clazz.getAnnotation(EsQueryIndex.class);
        if (ann == null) {
            if (method == null) {
                throw new EsEngineQueryException("undefine query-index @EsQueryIndex");
            }
            clazz = method.getDeclaringClass();
            ann = clazz.getAnnotation(EsQueryIndex.class);
            if (ann == null) {
                throw new EsEngineQueryException("undefine query-index @EsQueryIndex");
            }
        }
        String index = ann.value();
        QueryModel model = ann.model();
        String[] includeFields = ann.include();
        String[] excludeFields = ann.exclude();
        return new EsQueryIndexBean(index, model, includeFields, excludeFields);
    }

    /**
     * 解析入参实体类
     *
     * @param view        解析的查询对象
     * @param visitParent 是否解析父类属性
     * @return
     */
    public ParamParserResultModel read(Object view, boolean visitParent) {
        ParamParserResultModel parserResultModel = new ParamParserResultModel();
        List<EsQueryFieldBean> queryDesList = readNested(view, visitParent, parserResultModel.getRequestHooks());
        //按字段解析顺序排序
        List<EsQueryFieldBean> queryDesListOrder = queryDesList.stream().sorted(Comparator.comparing(EsQueryFieldBean::getOrder)).collect(Collectors.toList());
        parserResultModel.setQueryDesList(queryDesListOrder);
        return parserResultModel;
    }

    /**
     * 解析入参实体类 包括嵌套类的参数
     *
     * @param view        解析的查询对象
     * @param visitParent 是否解析父类属性
     * @return
     */
    public List<EsQueryFieldBean> readNested(Object view, boolean visitParent, List<Object> requestHooks) {
        List<EsQueryFieldBean> queryDesList = Lists.newArrayList();
        Map<Field, Object> fieldMap = new HashMap<>(16);
        Map<Field, Object> map = getNestedFields(fieldMap, view, visitParent, requestHooks);
        map.forEach((k, v) -> mapField(queryDesList, v, k));
        return queryDesList;
    }

    /**
     * 获取参数的field 包括嵌套字段的field
     *
     * @param fieldMap
     * @param view
     * @param visitParent
     * @return
     */
    public Map<Field, Object> getNestedFields(Map<Field, Object> fieldMap, Object view, boolean visitParent, List<Object> requestHooks) {
        Class<?> clazz = view.getClass();
        List<Field> fieldList = getFields(clazz, visitParent);
        for (Field field : fieldList) {
            //忽略的字段直接跳过
            if (field.isAnnotationPresent(Ignore.class)) {
                continue;
            }
            //解析嵌套类的属性
            if (field.isAnnotationPresent(Nested.class)) {
                if (ReflectionUtils.isBaseType(field.getType())) {
                    continue;
                } else {
                    field.setAccessible(true);
                    Object val;
                    try {
                        val = field.get(view);
                    } catch (IllegalAccessException e) {
                        throw new EsEngineQueryException("unable reach target field ", e);
                    }
                    //扩展嵌套对象自定义查询
                    Class<?> type = field.getType();
                    if (RequestHook.class.isAssignableFrom(type)) {
                        requestHooks.add(val);
                    }
                    if (Objects.nonNull(val)) {
                        getNestedFields(fieldMap, val, visitParent, requestHooks);
                    }
                }
            } else {
                fieldMap.put(field, view);
            }
        }
        return fieldMap;
    }

    /**
     * 解析属性  包括带有注解属性和 不带注解属性的默认解析
     *
     * @param queryDesList
     * @param view
     * @param field
     */
    private void mapField(List<EsQueryFieldBean> queryDesList, Object view, Field field) {
        Set<Annotation> annotationSet = Arrays.stream(field.getAnnotations())
                .filter(ann -> ann.annotationType().isAnnotationPresent(Query.class))
                .collect(Collectors.toSet());
        if (CollectionUtils.isNotEmpty(annotationSet)) {
            List<EsQueryFieldBean> queryDes = this.mapFieldAnn(field, view, annotationSet);
            queryDesList.addAll(queryDes);
        } else {
            //默认的全局忽略字段  不加term 查询
            //例如 log字段,当类添加了 @Slf4j 默认会加上log的本地变量  pageSize
            if (EsEngineConfig.getQueryIgnoreParam().contains(field.getName())) {
                return;
            }
            //没有添加注解的 基础类型设置默认 trem/trems查询
            if (EsEngineConfig.isIsBuildDefault()) {
                List<EsQueryFieldBean> queryDes = this.mapFieldAnnDefault(field, view);
                queryDesList.addAll(queryDes);
            }
        }
    }

    private List<EsQueryFieldBean> mapFieldAnn(Field field, Object viewObj, Set<Annotation> annotationSet) {
        final List<EsQueryFieldBean> res = Lists.newArrayList();
        for (Annotation ann : annotationSet) {
            //TODO 代码优化
            //检查参数类型
            fieldTypeCheck(field, viewObj);
            //扩展 不需要value值的参数
            Optional.ofNullable(parseValue(field, viewObj)).ifPresent(queryDes -> {
                this.parseAnn(queryDes, field, ann);
                res.add(queryDes);
            });
//            }
        }
        return res;
    }

    private void fieldTypeCheck(Field field, Object viewObj) {
        try {
            field.setAccessible(true);
            Object val = field.get(viewObj);
            Class<?> fieldType = field.getType();
            if (Objects.isNull(val)) {
                return;
            }
            //支持基础类型/List/扩展类型
            if (ReflectionUtils.isBaseTypeAndExtend(fieldType) || checkCollectionValueType(field, val) || val instanceof EsComplexParam) {
                //TODO 代码优化
                //TODO 类型转换器扩展 支持
                //TODO 参数校验器扩展***
            } else {
                throw new EsEngineQueryException("es annotation query field has error field, just support primitive type or their decorate type is List or EsComplexParam or add @Nested ; error field is: " + field.getName());
            }
        } catch (IllegalAccessException e) {
            throw new EsEngineQueryException("unable reach target field ", e);
        }
    }

    private List<EsQueryFieldBean> mapFieldAnnDefault(Field field, Object viewObj) {
        final List<EsQueryFieldBean> res = Lists.newArrayList();
        Optional.ofNullable(parseValueDefault(field, viewObj)).ifPresent(queryDes -> {
            this.parseAnnDefault(queryDes, field, viewObj);
            res.add(queryDes);
        });
        return res;
    }

    /**
     * 解析参数 value
     *
     * @param field
     * @param viewObj
     * @return
     */
    private EsQueryFieldBean parseValue(Field field, Object viewObj) {
        try {
            EsQueryFieldBean queryDes = new EsQueryFieldBean<>();
            field.setAccessible(true);
            Object val = field.get(viewObj);
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
        } catch (IllegalAccessException e) {
            throw new EsEngineQueryException("unable reach target field ", e);
        }
    }

    private EsQueryFieldBean parseValueDefault(Field field, Object viewObj) {
        try {
            EsQueryFieldBean queryDes = new EsQueryFieldBean<>();
            Class<?> fieldType = field.getType();
            field.setAccessible(true);
            Object val = field.get(viewObj);
            if (Objects.isNull(val)) {
                return null;
            }
            //支持基础类型/List
            if (ReflectionUtils.isBaseTypeAndExtend(fieldType) || checkCollectionValueType(field, val)) {
                //先临时加强对 Collection 和 String的校验 后续扩展参数校验器
                if (checkListAndString(val)) {
                    return null;
                }
                queryDes.setValue(val);
            } else {
                throw new EsEngineQueryException("es default query fields have wrong fields, Just support primitive type or their decorate type is List  or add @Nested; error field is: " + field.getName());
            }
            return queryDes;
        } catch (IllegalAccessException e) {
            throw new EsEngineQueryException("unable reach target field ", e);
        }
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
     * 判断是否是List类型切List元素为基本类型
     *
     * @param field
     * @param val
     * @return
     */
    private boolean checkCollectionValueType(Field field, Object val) {
        Predicate<Field> checkCollectionTypePredicate = f -> {
            ParameterizedType genericType = (ParameterizedType) f.getGenericType();
            Type[] actualType = genericType.getActualTypeArguments();
            String fullClassPath = actualType[0].getTypeName();
            Class<?> clazz;
            try {
                clazz = Class.forName(fullClassPath);
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
            return actualType.length == 1 && ReflectionUtils.isBaseType(clazz);
        };
        return (val instanceof List) && checkCollectionTypePredicate.test(field);
    }

    /**
     * 解析注解
     *
     * @param queryDes
     * @param field
     * @param targetAnn
     */
    private void parseAnn(EsQueryFieldBean queryDes, Field field, Annotation targetAnn) {
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
                column = field.getName();
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
     * 未加注解的 字段默认解析
     *
     * @param queryDes
     * @param field
     * @param viewObj
     */
    private void parseAnnDefault(EsQueryFieldBean queryDes, Field field, Object viewObj) {
        try {
            EsConnector esConnector = EsConnector.FILTER;
            queryDes.setLogicConnector(esConnector);
            String column = field.getName();
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
            Class<?> fieldType = field.getType();
            Object val = field.get(viewObj);
            String query = Strings.EMPTY;
            if (ReflectionUtils.isBaseType(fieldType)) {
                query = Term.class.getSimpleName();
                String queryField = WordUtils.uncapitalize(query);
                Term annotation = DefaultQueryModel.class.getField(queryField).getAnnotation(Term.class);
                queryDes.setExtAnnotation(annotation);
            } else if (checkCollectionValueType(field, val)) {
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
