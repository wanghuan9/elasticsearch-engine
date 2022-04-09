package com.elasticsearch.engine.elasticsearchengine.model.exception;

/**
 * @author wanghuan
 * @description: EsHelperQueryException
 * @date 2022-01-26 11:28
 */
public class EsHelperExecuteException extends RuntimeException {

    public EsHelperExecuteException() {
    }

    public EsHelperExecuteException(String message) {
        super(message);
    }

    public EsHelperExecuteException(String message, Throwable cause) {
        super(message, cause);
    }

    public EsHelperExecuteException(Throwable cause) {
        super(cause);
    }

    public EsHelperExecuteException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
