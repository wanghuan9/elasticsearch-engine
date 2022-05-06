package com.elasticsearch.engine.elasticsearchengine.common.utils;

/**
 * @author wanghuan
 * @description: ROOD
 * @date 2022-04-26 23:42
 */
public class NameExchangeUtil {

    /**
     * 驼峰式命名法
     */
    public static String toCamelCase(String s) {
        if (s == null) {
            return null;
        }
        s = s.toLowerCase();
        StringBuilder sb = new StringBuilder(s.length());
        boolean upperCase = false;
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);

            if (c == '_') {
                upperCase = true;
            } else if (upperCase) {
                sb.append(Character.toUpperCase(c));
                upperCase = false;
            } else {
                sb.append(c);
            }
        }
        return sb.toString();
    }
}
