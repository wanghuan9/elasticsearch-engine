package com.elasticsearch.engine.base.mapping.annotation.method;

import com.elasticsearch.engine.base.model.annotion.MethodQuery;
import org.elasticsearch.search.sort.SortOrder;

import java.lang.annotation.*;

/**
 * @author wanghuan
 * @description Order
 * @mail 958721894@qq.com
 * @date 2022-06-15 09:59
 */
@MethodQuery
@Inherited
@Documented
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Order {

    /**
     * 排序的字段
     * @return
     */
    String value() ;

    /**
     * 排序的顺序
     * @return
     */
    SortOrder type() default SortOrder.ASC;
}
