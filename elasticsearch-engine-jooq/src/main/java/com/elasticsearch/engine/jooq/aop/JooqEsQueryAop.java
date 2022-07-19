package com.elasticsearch.engine.jooq.aop;

import com.elasticsearch.engine.base.common.parse.sql.EsSqlQueryHelper;
import com.elasticsearch.engine.jooq.model.JooqBackDto;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;


/**
 * @author wanghuan
 * @description: LogAop
 * @date 2022-05-24 23:17
 */
@Slf4j
@Component
@Aspect
public class JooqEsQueryAop {

    @Resource
    private EsSqlQueryHelper esSqlQueryHelper;

    /**
     * 拦截添加了注解 @JpaEsQuery 的方法
     */
    @Pointcut("@annotation(com.elasticsearch.engine.jooq.annotion.JooqEsQuery)")
    public void esQueryCut() {
    }

    @Around(value = "esQueryCut()")
    public Object retryAdvice(ProceedingJoinPoint pjp) throws Throwable {
        MethodSignature signature = (MethodSignature) pjp.getSignature();
        return esSqlQueryHelper.esSqlQueryAopCommon(pjp, JooqBackDto.hasJooqBack(signature.getMethod()));
    }

}
