package com.elasticsearch.engine.extend.jpa;

import com.elasticsearch.engine.common.parse.sql.EsSqlQueryHelper;
import com.elasticsearch.engine.model.domain.BackDto;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.lang.reflect.Method;


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

    // 自己定义切点 拦截重试模方法
    @Pointcut("@annotation(com.elasticsearch.engine.model.annotion.JpaEsQuery)")
    public void esQueryCut() {
    }

    @Around(value = "esQueryCut()")
    public Object retryAdvice(ProceedingJoinPoint pjp) throws Throwable {
        MethodSignature signature = (MethodSignature) pjp.getSignature();
        Method method = signature.getMethod();
        return esSqlQueryHelper.esSqlQueryAopCommon(pjp, BackDto.hasJpaBack(method));
    }
}
