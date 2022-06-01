package com.elasticsearch.engine.model.exception;

/**
 * @author wanghuan
 * @description: EsHelperConfigException
 * @date 2022-01-26 11:28
 */
public class EsHelperConfigException extends RuntimeException {

    public EsHelperConfigException() {
    }

    public EsHelperConfigException(String message) {
        super(message);
    }

    public EsHelperConfigException(String message, Throwable cause) {
        super(message, cause);
    }

    public EsHelperConfigException(Throwable cause) {
        super(cause);
    }

    public EsHelperConfigException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
