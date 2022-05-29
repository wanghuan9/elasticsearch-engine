package com.elasticsearch.engine.elasticsearchengine.model.exception;

/**
 * @author wanghuan
 * @description: EsHelperQueryException
 * @date 2022-01-26 11:28
 */
public class EsHelperJpaExecuteException extends RuntimeException {

    public EsHelperJpaExecuteException() {
    }

    public EsHelperJpaExecuteException(String message) {
        super(message);
    }

    public EsHelperJpaExecuteException(String message, Throwable cause) {
        super(message, cause);
    }

    public EsHelperJpaExecuteException(Throwable cause) {
        super(cause);
    }

    public EsHelperJpaExecuteException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
