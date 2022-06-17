package com.elasticsearch.engine.base.mapping.annotation.method;


import com.elasticsearch.engine.base.model.annotion.MethodQuery;

import java.lang.annotation.*;

/**
* @author wanghuan
* @description Exclude
* @mail 958721894@qq.com       
* @date 2022/6/15 00:00 
*/
@MethodQuery
@Inherited
@Documented
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Exclude {

    String[] value() default {};
}
