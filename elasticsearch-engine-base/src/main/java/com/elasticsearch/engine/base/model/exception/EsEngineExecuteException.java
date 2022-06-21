package com.elasticsearch.engine.base.model.exception;

/**
 * @author wanghuan
 * @description: EsHelperQueryException
 * @date 2022-01-26 11:28
 */
public class EsEngineExecuteException extends RuntimeException {

    public EsEngineExecuteException() {
    }

    public EsEngineExecuteException(String message) {
        super(message);
    }

    public EsEngineExecuteException(String message, Throwable cause) {
        super(message, cause);
    }

    public EsEngineExecuteException(Throwable cause) {
        super(cause);
    }

    public EsEngineExecuteException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
