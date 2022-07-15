package com.elasticsearch.engine.base.common.utils;

/**
 * @author wanghuan
 * @description StringUtils
 * @mail 958721894@qq.com
 * @date 2022-07-15 22:24
 */
public class LocalStringUtils {

    /**
     * 替换string中的'`'
     *
     * @param str
     * @return
     */
    public static String replaceSlightPauseMark(String str) {
        return str.replaceAll("`", "");
    }

    /**
     * 替换string中的'.'
     *
     * @param str
     * @return
     */
    public static String replaceSpot(String str) {
        return str.replaceAll("\\.", "");
    }
}
