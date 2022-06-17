package com.elasticsearch.engine.base.common.utils;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
* @author wanghuan
* @description AnnotationUtils
* @mail 958721894@qq.com       
* @date 2022/6/9 14:11 
*/
public class AnnotationUtils {

    private static final HashSet<String> OBJECT_METHOD_FILTER = Sets.newHashSet(
            Arrays.stream(Annotation.class.getMethods()).map(Method::getName).collect(Collectors.toSet())
    );

    public static Map<String, Object> toMap(Annotation ann) {
        List<Method> annMethods = Arrays.stream(ann.getClass().getDeclaredMethods())
                .filter(m -> !OBJECT_METHOD_FILTER.contains(m.getName()))
                .collect(Collectors.toList());
        Map<String, Object> res = Maps.newHashMap();
        for (Method m : annMethods) {
            String key = m.getName();
            Object val = ReflectionUtils.methodInvoke(ann, m);
            res.put(key, val);
        }
        return res;
    }


}
