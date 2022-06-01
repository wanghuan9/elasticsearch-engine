package com.elasticsearch.engine.model.annotion;

import java.lang.annotation.*;

/**
 * @author wanghuan
 * @date 2022-01-26 11:28
 */
@Inherited
@Documented
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface EsQueryHandle {

    String queryType() default "";

    Class<? extends Annotation> value() default Annotation.class;
}
