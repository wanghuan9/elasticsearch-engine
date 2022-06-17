package com.elasticsearch.engine.common.utils;

import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;

/**
* @author wanghuan
* @description ThreadLocalUtil
* @mail 958721894@qq.com       
* @date 2022/6/17 15:30 
*/
public class ThreadLocalUtil {

    private static final ThreadLocal<Map<String, Object>> THREAD_LOCAL = ThreadLocal.withInitial(() -> new HashMap(4));

    public static Map<String, Object> getThreadLocal() {
        return THREAD_LOCAL.get();
    }

    public static <T> T get(String key) {
        Map map = (Map) THREAD_LOCAL.get();
        return (T) map.get(key);
    }

    public static <T> T get(String key, T defaultValue) {
        Map map = (Map) THREAD_LOCAL.get();
        return (T) map.get(key) == null ? defaultValue : (T) map.get(key);
    }

    public static void set(String key, Object value) {
        Map map = (Map) THREAD_LOCAL.get();
        map.put(key, value);
    }

    public static void set(Map<String, Object> keyValueMap) {
        Map map = (Map) THREAD_LOCAL.get();
        map.putAll(keyValueMap);
    }

    public static void remove() {
        THREAD_LOCAL.remove();
    }

    public static <T> T remove(String key) {
        if (StringUtils.isNotEmpty(key)) {
            Map map = (Map) THREAD_LOCAL.get();
            return (T) map.remove(key);

        }
        return null;
    }
}
