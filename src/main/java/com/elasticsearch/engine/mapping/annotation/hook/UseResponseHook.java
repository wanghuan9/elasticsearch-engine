package com.elasticsearch.engine.mapping.annotation.hook;

import java.lang.annotation.*;

/**
* @author wanghuan
* @description UseResponseHook
* @mail 958721894@qq.com       
* @date 2022/6/9 14:13 
*/
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
public @interface UseResponseHook {

    String value();

}
