package com.elasticsearch.engine.base.common.utils;

import com.elasticsearch.engine.base.model.emenu.DataType;
import com.elasticsearch.engine.base.model.exception.EsEngineExecuteException;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.util.*;

/**
* @author wanghuan
* @description BeanTools
* @mail 958721894@qq.com       
* @date 2022/6/17 15:30 
*/
public class BeanTools {
    public static Object mapToObject(Map map, Class<?> beanClass) throws Exception {
        if (map == null) {
            return null;
        }

        Object obj = beanClass.newInstance();

        Field[] fields = obj.getClass().getDeclaredFields();
        for (Field field : fields) {
            if (map.get(field.getName()) == null || StringUtils.isEmpty(map.get(field.getName()))) {
                continue;
            }
            int mod = field.getModifiers();
            if (Modifier.isStatic(mod) || Modifier.isFinal(mod)) {
                continue;
            }
            field.setAccessible(true);
            field.set(obj, map.get(field.getName()));
        }
        return obj;
    }

    public static <T> T typeMapToObject(Map<String, NameTypeValueMap> map, Class<T> beanClass) throws Exception {
        if (map == null) {
            return null;
        }
        T t = beanClass.newInstance();
        Field[] fields = t.getClass().getDeclaredFields();
        for (Field field : fields) {
            NameTypeValueMap nameTypeValueMap = map.get(field.getName());
            if (map.get(field.getName()) == null || nameTypeValueMap == null) {
                continue;
            }
            int mod = field.getModifiers();
            if (Modifier.isStatic(mod) || Modifier.isFinal(mod)) {
                continue;
            }
            field.setAccessible(true);
            if (!Objects.isNull(nameTypeValueMap.getValue())) {
                field.set(t, fieldTypeCovert(nameTypeValueMap.getDataType(), nameTypeValueMap.getValue(), field.getType()));
            }
        }
        return t;
    }

    public static String[] getNoValuePropertyNames(Object source) {
        Assert.notNull(source, "?????????????????????????????????");
        final BeanWrapper beanWrapper = new BeanWrapperImpl(source);
        PropertyDescriptor[] pds = beanWrapper.getPropertyDescriptors();

        Set<String> noValuePropertySet = new HashSet<>();
        Arrays.stream(pds).forEach(pd -> {
            Object propertyValue = beanWrapper.getPropertyValue(pd.getName());
            if (StringUtils.isEmpty(propertyValue)) {
                noValuePropertySet.add(pd.getName());
            } else {
                if (Iterable.class.isAssignableFrom(propertyValue.getClass())) {
                    Iterable iterable = (Iterable) propertyValue;
                    Iterator iterator = iterable.iterator();
                    if (!iterator.hasNext()) {
                        noValuePropertySet.add(pd.getName());
                    }
                }
                if (Map.class.isAssignableFrom(propertyValue.getClass())) {
                    Map map = (Map) propertyValue;
                    if (map.isEmpty()) {
                        noValuePropertySet.add(pd.getName());
                    }
                }
            }
        });
        String[] result = new String[noValuePropertySet.size()];
        return noValuePropertySet.toArray(result);
    }

    /**
     * es??????????????????
     *
     * @param dataType
     * @param value
     * @param beanClass
     * @param <T>
     * @return
     * @throws Exception
     */
    public static <T> Object fieldTypeCovert(DataType dataType, String value, Class<T> beanClass) throws Exception {
        if (dataType == DataType.date_type) {
            return DateUtils.parseToLocalDateTimeAuto(value);
        } else if (dataType == DataType.double_type) {
            if (beanClass.isAssignableFrom(BigDecimal.class)) {
                return BigDecimal.valueOf(Double.parseDouble(value));
            } else {
                return Double.valueOf(value);
            }
        } else if (dataType == DataType.byte_type) {
            return Byte.valueOf(value);
        } else if (dataType == DataType.boolean_type) {
            return Boolean.valueOf(value);
        } else if (dataType == DataType.integer_type) {
            return Integer.valueOf(value);
        } else if (dataType == DataType.float_type) {
            return Float.valueOf(value);
        } else if (dataType == DataType.long_type) {
            return Long.valueOf(value);
        } else if (dataType == DataType.keyword_type) {
            return String.valueOf(value);
        } else if (dataType == DataType.text_type) {
            return String.valueOf(value);
        } else if (dataType == DataType.short_type) {
            return Short.valueOf(value);
        } else {
            throw new EsEngineExecuteException("not support field type covert");
        }
    }

    public static class NameTypeValueMap {
        private String fieldName;
        private DataType dataType;
        private String value;

        public String getFieldName() {
            return fieldName;
        }

        public void setFieldName(String fieldName) {
            this.fieldName = fieldName;
        }

        public DataType getDataType() {
            return dataType;
        }

        public void setDataType(DataType dataType) {
            this.dataType = dataType;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }
}
