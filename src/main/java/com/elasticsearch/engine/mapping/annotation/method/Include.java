package com.elasticsearch.engine.mapping.annotation.method;

import com.elasticsearch.engine.model.annotion.MethodQuery;

import java.lang.annotation.*;

/**
* @author wanghuan
* @description Include
* @mail 958721894@qq.com       
* @date 2022/6/15 00:00 
*/
@MethodQuery
@Inherited
@Documented
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Include {

    String[] value() default {};

}
