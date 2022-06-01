package com.elasticsearch.engine.model.annotion;

import java.lang.annotation.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * @author wanghuan
 * @description: Sign 标记注解
 * 标记注解不解析value,只解析注解值
 * 需要设置 value值不为空,查询条件才会生效, 但是设置的value不会被解析,仅仅标记是否添加该条件
 * 所以value可以任意设置, 但是注意 string 不能为空串,数组类型不能为null
 * @date 2022-01-26 11:28
 */
@Inherited
@Documented
@Target({ElementType.ANNOTATION_TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface Sign {

    /**
     * Integer default value
     */
    Integer DEFAULT_INTER = 1;
    /**
     * Long default value
     */
    Long DEFAULT_LONG = 1L;
    /**
     * String default value
     */
    String DEFAULT_STRING = "1";
    /**
     * LocalDateTime default value
     */
    LocalDateTime DEFAULT_LOCAL_DATE_TIME = LocalDateTime.now();
    /**
     * LocalDate default value
     */
    LocalDate DEFAULT_LOCAL_DATE = LocalDate.now();
}
