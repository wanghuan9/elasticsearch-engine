package com.elasticsearch.engine.base.common.parse.sql;

import com.elasticsearch.engine.base.common.utils.CaseFormatUtils;
import com.elasticsearch.engine.base.common.utils.DateUtils;
import com.elasticsearch.engine.base.common.utils.ReflectionUtils;
import com.elasticsearch.engine.base.config.EsEngineConfig;
import com.elasticsearch.engine.base.model.domain.SqlResponse;
import com.elasticsearch.engine.base.model.emenu.DataType;
import com.elasticsearch.engine.base.model.exception.EsEngineExecuteException;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * @author wanghuan
 * @description BeanTools
 * @mail 958721894@qq.com
 * @date 2022/6/17 15:30
 */
public class SqlResponseParseHelper {

    /**
     * 将sql查询结果转换为指定类
     *
     * @param columns
     * @param rows
     * @param clazz
     * @param <T>
     * @return
     * @throws Exception
     */
    public static <T> T generateObjBySqlReps(List<SqlResponse.ColumnsDTO> columns, List<String> rows, Class<T> clazz, Boolean isExtendQuery) throws Exception {
        if (rows.size() != columns.size()) {
            throw new Exception("sql column not match");
        }
        //单个结果: count,sum 结果转换
        boolean check = rows.size() == 1 && (ReflectionUtils.isBaseType(clazz) || clazz.equals(BigDecimal.class));
        if (check) {
            return (T) SqlResponseParseHelper.fieldTypeCovert(DataType.getDataTypeByStr(columns.get(0).getType()), rows.get(0), clazz);
        }
        //entity listEntity
        Map<String, SqlResponseParseHelper.NameTypeValueMap> valueMap = new HashMap(32);
        for (int i = 0; i < rows.size(); i++) {
            SqlResponseParseHelper.NameTypeValueMap m = new SqlResponseParseHelper.NameTypeValueMap();
            m.setDataType(DataType.getDataTypeByStr(columns.get(i).getType()));
            String paramName = columns.get(i).getName();
            //是否下划线转驼峰转 
            if (EsEngineConfig.isNamingStrategy()) {
                paramName = CaseFormatUtils.underscoreToCamel(paramName);
            }
            m.setFieldName(paramName);
            m.setValue(rows.get(i));
            valueMap.put(paramName, m);
        }
        T t = (T) SqlResponseParseHelper.typeMapToObject(valueMap, clazz);
        return t;
    }

    /**
     * map to object
     *
     * @param map
     * @param beanClass
     * @param <T>
     * @return
     * @throws Exception
     */
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


    /**
     * es参数类型转化
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
