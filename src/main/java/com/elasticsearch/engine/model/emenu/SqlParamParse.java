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
    JAP_SQL_PARAM("jpa", "\\?", "", "?"),
    ANN_SQL_PARAM("ann", "\\#\\{%s\\}", "\\$\\{%s\\}", "#{");


    private String type;
    private String regexStr;
    private String likeRegexStr;
    private String placeHolder;

    SqlParamParse(String type, String regexStr, String likeRegexStr, String placeHolder) {
        this.type = type;
        this.regexStr = regexStr;
        this.likeRegexStr = likeRegexStr;
        this.placeHolder = placeHolder;
    }

}
