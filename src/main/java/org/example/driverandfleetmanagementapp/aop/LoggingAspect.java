package org.example.driverandfleetmanagementapp.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.stereotype.Component;
import java.util.Arrays;


@Aspect
@Component
@Slf4j
@EnableAspectJAutoProxy
public class LoggingAspect {


    @Pointcut("execution(* org.example.driverandfleetmanagementapp.controller..*.*(..)) || " +
            "execution(* org.example.driverandfleetmanagementapp.service..*.*(..)) || " +
            "execution(* org.example.driverandfleetmanagementapp.config..*.*(..)) || " +
            "execution(* org.example.driverandfleetmanagementapp.security..*.*(..))")
    public void applicationPackagePointcut() {
    }


    @Pointcut("execution(* org.example.driverandfleetmanagementapp.exception..*.*(..))")
    public void exceptionPackagePointcut() {
    }



    @Around("applicationPackagePointcut()")
    public Object logAround(ProceedingJoinPoint joinPoint) throws Throwable {
        String className = joinPoint.getTarget().getClass().getSimpleName();
        String methodName = joinPoint.getSignature().getName();

        log.info("Starting {} in class {}", methodName, className);
        if (log.isDebugEnabled()) {
            String args = Arrays.toString(joinPoint.getArgs());
            log.debug("Method: {}.{} called with arguments: {}", className, methodName, args);
        }


        Object result = joinPoint.proceed();

        log.info("Completed {} in class {}", methodName, className);
        if (log.isDebugEnabled()) {
            log.debug("Method: {}.{} completed execution", className, methodName);
        }

        return result;
    }


    @AfterThrowing(pointcut = "exceptionPackagePointcut()", throwing = "exception")
    public void logException(JoinPoint joinPoint, Exception exception) {
        String className = joinPoint.getTarget().getClass().getSimpleName();
        String methodName = joinPoint.getSignature().getName();

        log.error("Exception in {}.{}: {}",
                className,
                methodName,
                exception.getMessage(),
                exception
        );
    }
}
