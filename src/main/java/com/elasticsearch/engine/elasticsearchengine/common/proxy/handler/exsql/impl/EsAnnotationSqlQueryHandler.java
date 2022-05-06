package com.elasticsearch.engine.elasticsearchengine.common.proxy.handler.exsql.impl;

import com.elasticsearch.engine.elasticsearchengine.common.proxy.enums.EsSqlQueryEnum;
import com.elasticsearch.engine.elasticsearchengine.common.proxy.handler.exannotation.AnnotationQueryCommon;
import com.elasticsearch.engine.elasticsearchengine.common.proxy.handler.exsql.EsSqlQueryHandler;
import com.elasticsearch.engine.elasticsearchengine.common.queryhandler.sql.EsSqlExecuteHandler;
import com.elasticsearch.engine.elasticsearchengine.common.utils.ReflectionUtils;
import com.elasticsearch.engine.elasticsearchengine.common.utils.ThreadLocalUtil;
import com.elasticsearch.engine.elasticsearchengine.model.annotion.EsQuery;
import com.elasticsearch.engine.elasticsearchengine.model.constant.CommonConstant;
import com.elasticsearch.engine.elasticsearchengine.model.domain.BaseESRepository;
import com.elasticsearch.engine.elasticsearchengine.model.exception.EsHelperExecuteException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author wanghuan
 * @description: ROOD
 * @date 2022-04-26 23:13
 */
@Slf4j
@Component
public class EsAnnotationSqlQueryHandler implements EsSqlQueryHandler {

    @Resource
    private EsSqlExecuteHandler esSqlExecuteHandler;

    @Override
    public Boolean matching(EsSqlQueryEnum factory) {
        return EsSqlQueryEnum.ANNOTATION_QUERY.equals(factory);
    }

    @Override
    public Object handle(Object proxy, Method method, Object[] args) {
        String prefix = ThreadLocalUtil.get(CommonConstant.INTERFACE_METHOD_NAME);
        //方法返回值
        Class<?> returnType = method.getReturnType();
        //方法返回值的泛型
        Class<?> returnGenericType = AnnotationQueryCommon.getReturnGenericType(method);
        //获取到Repository泛型的Entity类
        Class<?> retEntityClass = AnnotationQueryCommon.getClazzImplClassGeneric(method.getDeclaringClass(), BaseESRepository.class);
        EsQuery esQuery = method.getAnnotation(EsQuery.class);
        if (Objects.isNull(esQuery) || StringUtils.isEmpty(esQuery.value())) {
            throw new EsHelperExecuteException(prefix + "@EsQuery 注解不存在或参数为空");
        }
        // 获取方法的所有参数
        Map<String, Object> paramMap = getParamMap(method, args);
        String sql = renderString(esQuery.value(), paramMap);

        List<?> list;
        if (List.class.isAssignableFrom(returnType) && Objects.nonNull(returnGenericType)) {
            list = esSqlExecuteHandler.queryBySQL(sql, returnGenericType);
        } else {
            list = esSqlExecuteHandler.queryBySQL(sql, returnType);
        }

        if (List.class.isAssignableFrom(returnType)) {
            return list;
        } else {
            if (list.size() > 0) {
                return list.get(0);
            }
            return null;
        }
    }

    /**
     * 获取方法的所有参数
     *
     * @param method
     * @param args
     * @return
     */
    private Map<String, Object> getParamMap(Method method, Object[] args) {
        Map<String, Object> map = new HashMap<>();
        Parameter[] parameters = method.getParameters();
        for (int i = 0; i < parameters.length; i++) {
            Parameter parameter = parameters[i];
            Object val = args[i];
            if (parameter.getType().isAssignableFrom(List.class) && ReflectionUtils.checkCollectionValueType(parameter, val)) {
                if (val instanceof List) {
                    List listParam = (List) val;
                    StringBuffer sb = new StringBuffer();
                    if (!listParam.isEmpty()) {
                        listParam.forEach(item -> {
                            sb.append("'" + item + "'");
                        });
                    }
                    map.put(parameter.getName(), sb.toString());
                }
            } else {
                String arg = "'" + val + "'";
                map.put(parameter.getName(), arg);
            }
        }
        return map;
    }

    /**
     * 拼接sql的参数
     *
     * @param content
     * @param map
     * @return
     */
    public String renderString(String content, Map<String, Object> map) {
        Set<Map.Entry<String, Object>> entries = map.entrySet();
        for (Map.Entry<String, Object> e : map.entrySet()) {
            String regex = String.format("\\#\\{%s\\}", e.getKey());
            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(content);
            content = matcher.replaceAll(e.getValue().toString());
        }
        return content;
    }
}
