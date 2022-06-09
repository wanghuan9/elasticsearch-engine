package com.elasticsearch.engine.model.exception;

/**
 * @author wanghuan
 * @description: EsHelperQueryException
 * @date 2022-01-26 11:28
 */
public class EsEngineQueryException extends RuntimeException {

    public EsEngineQueryException() {
    }

    public EsEngineQueryException(String message) {
        super(message);
    }

    public EsEngineQueryException(String message, Throwable cause) {
        super(message, cause);
    }

    public EsEngineQueryException(Throwable cause) {
        super(cause);
    }

    public EsEngineQueryException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
