package com.elasticsearch.engine.elasticsearchengine.model.emenu;

/**
 * program: esdemo
 * description: es字段数据结构
 * author: X-Pacific zhang
 * create: 2019-01-25 16:58
 **/
public enum DataType {
    keyword_type, text_type, byte_type, short_type, integer_type, long_type, float_type, double_type, boolean_type, date_type, nested_type, geo_point_type;


    public static DataType getDataTypeByStr(String str) {
        if (str.equals("keyword")) {
            return keyword_type;
        } else if (str.equals("text")) {
            return text_type;
        } else if (str.equals("byte")) {
            return byte_type;
        } else if (str.equals("short")) {
            return short_type;
        } else if (str.equals("integer")) {
            return integer_type;
        } else if (str.equals("long")) {
            return long_type;
        } else if (str.equals("float")) {
            return float_type;
        } else if (str.equals("double")) {
            return double_type;
        } else if (str.equals("boolean")) {
            return boolean_type;
        } else if (str.equals("date") || str.equals("datetime")) {
            return date_type;
        } else {
            return text_type;
        }
    }
}
