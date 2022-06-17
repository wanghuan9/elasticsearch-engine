package com.elasticsearch.engine.jooq.aop;

import com.elasticsearch.engine.base.common.parse.sql.EsSqlQueryHelper;
import com.elasticsearch.engine.base.common.utils.ThreadLocalUtil;
import com.elasticsearch.engine.base.config.EsEngineConfig;
import com.elasticsearch.engine.base.model.constant.CommonConstant;
import com.elasticsearch.engine.base.model.domain.BackDto;
import com.elasticsearch.engine.base.model.exception.EsEngineJpaExecuteException;
import com.elasticsearch.engine.jooq.model.JooqBackDto;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Objects;


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

    // 自己定义切点 拦截重试模方法
    @Pointcut("@annotation(com.elasticsearch.engine.jooq.annotion.JooqEsQuery)")
    public void esQueryCut() {
    }

    @Around(value = "esQueryCut()")
    public Object retryAdvice(ProceedingJoinPoint pjp) throws Throwable {
        MethodSignature signature = (MethodSignature) pjp.getSignature();
        Method method = signature.getMethod();
        Object[] args = pjp.getArgs();
        //不走es查询直接返回
        if(!EsEngineConfig.isEsquery(method)){
            return pjp.proceed(args);
        }
        BackDto backDto = JooqBackDto.hasJooQBack(method);
        Object result = null;
        try {
            ThreadLocalUtil.set(CommonConstant.IS_ES_QUERY, Boolean.TRUE);
            result = pjp.proceed(args);
        } catch (EsEngineJpaExecuteException e) {
            if (Objects.nonNull(backDto)) {
                List<?> esResult = esSqlQueryHelper.esQueryBack(method, e.getMessage(), args, backDto);
                if (CollectionUtils.isEmpty(esResult)) {
                    return result;
                }
                result = pjp.proceed(args);
            } else {
                result = esSqlQueryHelper.esQuery(method, e.getMessage(), args, backDto);
            }
        } finally {
            ThreadLocalUtil.remove(CommonConstant.IS_ES_QUERY);
            ThreadLocalUtil.remove(CommonConstant.BACK_QUERY_SQL);
        }
        return result;
    }

}
