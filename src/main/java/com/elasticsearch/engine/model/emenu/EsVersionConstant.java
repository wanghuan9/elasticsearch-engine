package com.elasticsearch.engine.model.emenu;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.Optional;

/**
 * @author wanghuan
 * @description: ROOD
 * @date 2022-04-28 17:35
 */
@Getter
public enum EsVersionConstant {

    /**
     * 
     */
    ES_VERSION_6(6, "/_xpack/sql?format=", "/_xpack/sql/translate?format=", "yyyy-MM-dd'T'HH:mm:ss.SSS"),
    ES_VERSION_7(7, "/_sql?format=", "/_sql/translate?format=", "yyyy - MM - dd'T'HH:mm:ss.SSS'Z'");

    private Integer version;
    private String sqlQueryPrefix;
    private String sqlTranslatePrefix;
    private String dateformat;

    EsVersionConstant(Integer version, String sqlQueryPrefix, String sqlTranslatePrefix, String dateformat) {
        this.version = version;
        this.sqlQueryPrefix = sqlQueryPrefix;
        this.dateformat = dateformat;
        this.sqlTranslatePrefix = sqlTranslatePrefix;
    }

    public static String getSqlQueryPrefix(Integer version) {
        Optional<EsVersionConstant> assetPowerBankItemChangeEnum = Arrays.stream(EsVersionConstant.values())
                .filter(c -> c.getVersion().equals(version)).findFirst();
        return assetPowerBankItemChangeEnum.map(EsVersionConstant::getSqlQueryPrefix).orElse(StringUtils.EMPTY);
    }

    @JsonCreator
    public static EsVersionConstant of(Integer version) {
        Optional<EsVersionConstant> assetPowerBankItemChangeEnum = Arrays.stream(EsVersionConstant.values())
                .filter(c -> c.getVersion().equals(version)).findFirst();
        return assetPowerBankItemChangeEnum.orElse(null);
    }

    @JsonValue
    public Integer getVersion() {
        return version;
    }
}
