package com.elasticsearch.engine.model.exception;

/**
 * @author wanghuan
 * @description: EsHelperConfigException
 * @date 2022-01-26 11:28
 */
public class EsEngineConfigException extends RuntimeException {

    public EsEngineConfigException() {
    }

    public EsEngineConfigException(String message) {
        super(message);
    }

    public EsEngineConfigException(String message, Throwable cause) {
        super(message, cause);
    }

    public EsEngineConfigException(Throwable cause) {
        super(cause);
    }

    public EsEngineConfigException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
