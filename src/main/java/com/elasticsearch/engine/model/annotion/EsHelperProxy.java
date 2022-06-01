package com.elasticsearch.engine.model.annotion;

import java.lang.annotation.*;

/**
 * EsHelperProxy
 * <p>
 * author     JohenTeng
 * date      2021/9/17
 */
@Inherited
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface EsHelperProxy {

    /**
     * When phrase query-Define bean will visit it's parent
     */
    boolean visitParent() default true;

}
