package com.elasticsearch.engine.common.utils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.Map;

/**
 * ExtAnnBeanMapUtils
 * <p>
 * author     JohenTeng
 * date      2021/9/23
 */
public class ExtAnnBeanMapUtils {


    /**
     * phrase annotation to JavaBean
     *
     * @param annotation target Annotation
     * @param clazz      mapping class
     *                   return    target JavaBean(T)
     */
    public static Object mapping(Annotation annotation, Class<?> clazz) {
        Field[] extBeanFields = clazz.getDeclaredFields();
        Object extBean = ReflectionUtils.newInstance(clazz);
        Map<String, Object> annMapping = AnnotationUtils.toMap(annotation);
        for (Field field : extBeanFields) {
            field.setAccessible(true);
            String key = field.getName();
            Object val = annMapping.get(key);
            ReflectionUtils.setFieldValue(extBean, field, val, false);
        }
        return extBean;
    }

}
