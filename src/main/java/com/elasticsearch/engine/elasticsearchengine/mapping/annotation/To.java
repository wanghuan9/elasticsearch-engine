package com.elasticsearch.engine.elasticsearchengine.mapping.annotation;


import com.elasticsearch.engine.elasticsearchengine.model.annotion.Base;
import com.elasticsearch.engine.elasticsearchengine.model.annotion.Query;

import java.lang.annotation.*;

/**
 * @author wanghuan
 * @description: To
 * @date 2022-01-26 11:28
 */
@Query
@Inherited
@Documented
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface To {

    Base value() default @Base;

    /**
     * es7格式化
     *
     * @return
     */
    String format() default "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";

    String timeZone() default "GMT+8";

    /**
     * es6 格式化
     * @return
     */
//    String format() default "yyyy-MM-dd'T'HH:mm:ss.SSS";
//    String timeZone() default "+08:00";

    /**
     * 多个range时, 使用 group标识一对匹配的 from和to
     *
     * @return
     */
    int group() default 0;
}
