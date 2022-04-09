package com.elasticsearch.engine.elasticsearchengine.common.parse;


import com.elasticsearch.engine.elasticsearchengine.common.GlobalConfig;
import com.elasticsearch.engine.elasticsearchengine.common.utils.ReflectionUtils;
import com.elasticsearch.engine.elasticsearchengine.hook.RequestHook;
import com.elasticsearch.engine.elasticsearchengine.mapping.annotation.Term;
import com.elasticsearch.engine.elasticsearchengine.mapping.annotation.Terms;
import com.elasticsearch.engine.elasticsearchengine.model.annotion.*;
import com.elasticsearch.engine.elasticsearchengine.model.constant.CommonConstant;
import com.elasticsearch.engine.elasticsearchengine.model.domain.*;
import com.elasticsearch.engine.elasticsearchengine.model.emenu.EsConnector;
import com.elasticsearch.engine.elasticsearchengine.model.emenu.QueryModel;
import com.elasticsearch.engine.elasticsearchengine.model.exception.EsHelperConfigException;
import com.elasticsearch.engine.elasticsearchengine.model.exception.EsHelperQueryException;
import com.google.common.base.CaseFormat;
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
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
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
    public EsQueryIndexBean getIndex(Object view) {
        Class<?> clazz = view.getClass();
        EsQueryIndex ann = clazz.getAnnotation(EsQueryIndex.class);
        if (ann == null) {
            throw new EsHelperQueryException("undefine query-index @EsQueryIndex");
        }
        String index = ann.index();
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
        Map<Field, Object> fieldMap = new HashMap<>();
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
        List<Field> fieldList = this.getFields(clazz, visitParent);
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
                        throw new EsHelperQueryException("unable reach target field ", e);
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
            if (GlobalConfig.QUERY_IGNORE_PARAM.contains(field.getName())) {
                return;
            }
            //没有添加注解的 基础类型设置默认 trem/trems查询
            if (GlobalConfig.IS_BUILD_DEFAULT) {
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
            if (ReflectionUtils.isBaseType(fieldType) || checkCollectionValueType(field, val) || val instanceof EsComplexParam || paramTypeExtends(val)) {
                //TODO 代码优化
                //TODO 类型转换器扩展 支持
                //TODO 参数校验器扩展***
            } else {
                throw new EsHelperQueryException("Es Annotation Query Field at an Error-Type Field, Just support Primitive-type or their Decorate-type is List or EsComplexParam or add @Nested ; error field is: " + field.getName());
            }
        } catch (IllegalAccessException e) {
            throw new EsHelperQueryException("unable reach target field ", e);
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
            throw new EsHelperQueryException("unable reach target field ", e);
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
            if (ReflectionUtils.isBaseType(fieldType) || checkCollectionValueType(field, val) || paramTypeExtends(val)) {
                //先临时加强对 Collection 和 String的校验 后续扩展参数校验器
                if (checkListAndString(val)) {
                    return null;
                }
                queryDes.setValue(val);
            } else {
                throw new EsHelperQueryException("Es Default Query Field at an Error-Type Field, Just support Primitive-type or their Decorate-type is List  or add @Nested; error field is: " + field.getName());
            }
            return queryDes;
        } catch (IllegalAccessException e) {
            throw new EsHelperQueryException("unable reach target field ", e);
        }
    }

    /**
     * 临时扩展类型
     *
     * @param val
     * @return
     */
    private boolean paramTypeExtends(Object val) {
        return val instanceof LocalDateTime || val instanceof LocalDate || val instanceof BigDecimal;
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


    private boolean checkCollectionValueType(Field field, Object val) {
        Predicate<Field> checkCollectionTypePredicate = f -> {
            ParameterizedType genericType = (ParameterizedType) f.getGenericType();
            Type[] actualType = genericType.getActualTypeArguments();
            return actualType.length == 1 && ReflectionUtils.isBaseType(actualType[0].getTypeName().getClass());
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
                throw new EsHelperQueryException("ES-QUERY-LOGIC-CONNECTOR cant be null");
            }
            queryDes.setLogicConnector(esConnector);

            String column = ann.name();
            if (StringUtils.isBlank(column)) {
                column = field.getName();
                //TODO 优化
                //设置list后缀
                GlobalConfig.QUERY_PARAM_SUFFIX.addAll(GlobalConfig.QUERY_PARAM_PREFIX);
                for (String fix : GlobalConfig.QUERY_PARAM_SUFFIX) {
                    if (column.contains(fix)) {
                        column = column.replace(fix, "");
                        break;
                    }
                }
                //若设置默认下划线
                if (GlobalConfig.QUERY_PARAM_IS_LOWER_UNDERSCORE) {
                    column = CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, column);
                }
            }
            queryDes.setField(column);
            queryDes.setOrder(ann.order());
            String query = targetAnn.annotationType().getSimpleName();
            if (StringUtils.isBlank(query)) {
                throw new EsHelperQueryException("QUERY-TYPE missing, it's necessary");
            }
            queryDes.setQueryType(query);

        } catch (Exception e) {
            throw new EsHelperConfigException("annotation analysis Error, cause:", e);
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
            GlobalConfig.QUERY_PARAM_SUFFIX.addAll(GlobalConfig.QUERY_PARAM_PREFIX);
            for (String fix : GlobalConfig.QUERY_PARAM_SUFFIX) {
                if (column.contains(fix)) {
                    column = column.replace(fix, "");
                    break;
                }
            }
            //若设置默认下划线
            if (GlobalConfig.QUERY_PARAM_IS_LOWER_UNDERSCORE) {
                column = CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, column);
            }
            queryDes.setField(column);
            Class<?> fieldType = field.getType();
            Object val = field.get(viewObj);
            String query = Strings.EMPTY;
            if (ReflectionUtils.isBaseType(fieldType)) {
                query = WordUtils.uncapitalize(Term.class.getSimpleName());
                Term annotation = DefaultQueryModel.class.getField(query).getAnnotation(Term.class);
                queryDes.setExtAnnotation(annotation);
            } else if (checkCollectionValueType(field, val)) {
                query = WordUtils.uncapitalize(Terms.class.getSimpleName());
                Terms annotation = DefaultQueryModel.class.getField(query).getAnnotation(Terms.class);
                queryDes.setExtAnnotation(annotation);
            }
            if (StringUtils.isBlank(query)) {
                throw new EsHelperQueryException("QUERY-TYPE missing, it's necessary");
            }
            queryDes.setQueryType(query);
        } catch (Exception e) {
            throw new EsHelperConfigException("annotation analysis Error, cause:", e);
        }
    }

}
