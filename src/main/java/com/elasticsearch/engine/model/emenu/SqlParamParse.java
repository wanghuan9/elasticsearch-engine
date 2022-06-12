package com.elasticsearch.engine.model.emenu;

import lombok.Getter;

/**
 * @author wanghuan
 * @description: ROOD
 * @date 2022-05-29 21:36
 */
@Getter
public enum SqlParamParse {

    /**
     *
     */
    JAP_SQL_PARAM("jpa", "?", "\\?", "", "","?"),
    ANN_SQL_PARAM("ann", "#{%s}", "\\#\\{%s\\}", "${%s}","\\$\\{%s\\}", "#{");


    private String type;
    private String formatStr;
    private String regexStr;
    private String likeformatStr;
    private String likeRegexStr;
    private String placeHolder;

    SqlParamParse(String type, String formatStr, String regexStr, String likeformatStr, String likeRegexStr, String placeHolder) {
        this.type = type;
        this.formatStr = formatStr;
        this.regexStr = regexStr;
        this.likeformatStr = likeformatStr;
        this.likeRegexStr = likeRegexStr;
        this.placeHolder = placeHolder;
    }

    /**
     * 替换字符串中的.
     *
     * @param regexStr
     * @return
     */
    public static  String getRegex(String regexStr) {
        return regexStr.replaceAll("\\.", "\\\\.");
    }

}
