package com.elasticsearch.engine.jpa.aop;

import com.elasticsearch.engine.base.common.parse.sql.EsSqlQueryHelper;
import com.elasticsearch.engine.jpa.model.JpaBackDto;
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
public class JpaEsQueryAop {

    @Resource
    private EsSqlQueryHelper esSqlQueryHelper;

    /**
     * 拦截添加了注解 @JpaEsQuery 的方法
     */
    @Pointcut("@annotation(com.elasticsearch.engine.jpa.annotion.JpaEsQuery)")
    public void esQueryCut() {
    }

    @Around(value = "esQueryCut()")
    public Object retryAdvice(ProceedingJoinPoint pjp) throws Throwable {
        MethodSignature signature = (MethodSignature) pjp.getSignature();
        return esSqlQueryHelper.esSqlQueryAopCommon(pjp, JpaBackDto.hasJpaBack(signature.getMethod()));
    }
}
