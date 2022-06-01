package com.elasticsearch.engine.model.exception;

/**
 * @author wanghuan
 * @description: EsHelperQueryException
 * @date 2022-01-26 11:28
 */
public class EsHelperQueryException extends RuntimeException {

    public EsHelperQueryException() {
    }

    public EsHelperQueryException(String message) {
        super(message);
    }

    public EsHelperQueryException(String message, Throwable cause) {
        super(message, cause);
    }

    public EsHelperQueryException(Throwable cause) {
        super(cause);
    }

    public EsHelperQueryException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
