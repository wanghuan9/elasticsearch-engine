package com.elasticsearch.engine.mapping.annotation;


import com.elasticsearch.engine.model.annotion.Base;
import com.elasticsearch.engine.model.annotion.Query;

import java.lang.annotation.*;

/**
 * @author wanghuan
 * @description: Range
 * @date 2022-01-26 11:28
 */
@Query
@Inherited
@Documented
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface Range {

    //表示左右包含  >= L and <= G
    String LE_GE = "[L,G]";
    //表示左右都不包含 >L and < G
    String L_G = "(L,G)";
    //表示左右都不包含 >=L and < G
    String LE_G = "[L,G)";
    //表示左右都不包含 >L and <= G
    String L_GE = "(L,G]";
    //表示 from to
    String F_T = "F_T";

    Base value() default @Base;

    /**
     * range-query both side relation
     * {@value LE_GE}
     * {@value L_G}
     * {@value LE_G}
     * {@value L_GE}
     * {@value F_T}
     * return
     */
    String tag() default L_G;

    String format() default "";

    String timeZone() default "";

    //within、contains。intersects
    String relation() default "";

    boolean includeLower() default false;

    boolean includeUpper() default false;
}
