package com.example.book.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Aspect
@Component
public class LoggingAspect {

    private static final Logger log =
            LoggerFactory.getLogger(LoggingAspect.class);

    @Pointcut("execution(* com.example.book.service.*.*(..))")
    public void serviceLayer() {
    }

    @Around("serviceLayer()")
    public Object logServiceMethod(ProceedingJoinPoint joinPoint)
            throws Throwable {

        String className =
                joinPoint.getTarget().getClass().getSimpleName();

        String methodName = joinPoint.getSignature().getName();

        log.info(
                ">> Entering {}.{}() with args={}",
                className,
                methodName,
                Arrays.toString(joinPoint.getArgs())
        );

        long startTime = System.currentTimeMillis();

        try {
            Object result = joinPoint.proceed();
            long executionTime = System.currentTimeMillis() - startTime;

            log.info(
                    "<< Exiting {}.{}() with result={} in {} ms",
                    className,
                    methodName,
                    result,
                    executionTime
            );

            return result;
        } catch (Throwable ex) {
            long executionTime = System.currentTimeMillis() - startTime;

            log.warn(
                    "!! Exception in {}.{}() after {} ms: {}",
                    className,
                    methodName,
                    executionTime,
                    ex.getMessage()
            );

            throw ex;
        }
    }
}
