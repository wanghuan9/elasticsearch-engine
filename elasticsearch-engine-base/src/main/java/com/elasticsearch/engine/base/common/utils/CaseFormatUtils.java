package com.elasticsearch.engine.base.common.utils;

import com.google.common.base.CaseFormat;
import org.apache.commons.lang3.StringUtils;

/**
 * @author wanghuan
 * @description CaseFormatUtils
 * @mail 958721894@qq.com
 * @date 2022-06-03 22:12
 */
public class CaseFormatUtils {

    /**
     * 下划线转驼峰
     *
     * @param columnName
     * @return
     */
    public static String underscoreToCamel(String columnName) {
        if (StringUtils.isNotEmpty(columnName) && columnName.contains("_")) {
            return CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, columnName);
        }
        return columnName;
    }

    /**
     * 驼峰转下划线
     *
     * @param columnName
     * @return
     */
    public static String camelToUnderscore(String columnName) {
        if (StringUtils.isNotEmpty(columnName) && isContainUpperCase(columnName)) {
            return CaseFormat.LOWER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, columnName);
        }
        return columnName;
    }

    /**
     * 判断string中是否包含大写字母
     *
     * @param str
     * @return
     */
    public static boolean isContainUpperCase(String str) {
        StringBuffer buf = new StringBuffer(str);
        for (int i = 0; i < buf.length(); i++) {
            if (Character.isUpperCase(buf.charAt(i))) {
                return true;
            }
        }
        return false;
    }
}
