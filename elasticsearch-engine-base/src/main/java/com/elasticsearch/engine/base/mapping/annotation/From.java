package com.elasticsearch.engine.base.mapping.annotation;


import com.elasticsearch.engine.base.model.annotion.Base;
import com.elasticsearch.engine.base.model.annotion.Query;

import java.lang.annotation.*;

/**
 * @author wanghuan
 * @description: From
 * @date 2022-01-26 11:28
 */
@Query
@Inherited
@Documented
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface From {


    Base value() default @Base;

    /**
     * es7 格式化
     *
     * @return
     */
    String format() default "";

    String timeZone() default "";

    /**
     * 多个range时, 使用 group标识一对匹配的 from和to
     *
     * @return
     */
    int group() default 0;
}
