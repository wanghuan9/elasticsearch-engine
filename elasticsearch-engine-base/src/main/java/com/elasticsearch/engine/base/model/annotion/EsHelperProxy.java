package com.elasticsearch.engine.base.model.annotion;

import java.lang.annotation.*;

/**
* @author wanghuan
* @description EsHelperProxy
* @mail 958721894@qq.com       
* @date 2022/6/9 14:09 
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
