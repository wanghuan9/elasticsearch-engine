package com.elasticsearch.engine.mapping.annotation.method;

import com.elasticsearch.engine.model.annotion.MethodQuery;

import java.lang.annotation.*;

/**
* @author wanghuan
* @description Size
* @mail 958721894@qq.com       
* @date 2022/6/14 23:58 
*/
@MethodQuery
@Inherited
@Documented
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Size {
    
    int value() default 1000;
}
