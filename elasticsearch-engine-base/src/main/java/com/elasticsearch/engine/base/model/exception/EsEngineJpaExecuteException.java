package com.elasticsearch.engine.base.model.exception;

/**
 * @author wanghuan
 * @description: EsHelperQueryException
 * @date 2022-01-26 11:28
 */
public class EsEngineJpaExecuteException extends RuntimeException {

    public EsEngineJpaExecuteException() {
    }

    public EsEngineJpaExecuteException(String message) {
        super(message);
    }

    public EsEngineJpaExecuteException(String message, Throwable cause) {
        super(message, cause);
    }

    public EsEngineJpaExecuteException(Throwable cause) {
        super(cause);
    }

    public EsEngineJpaExecuteException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
