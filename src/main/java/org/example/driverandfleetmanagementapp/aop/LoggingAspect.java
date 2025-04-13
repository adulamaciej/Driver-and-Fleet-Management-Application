package org.example.driverandfleetmanagementapp.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Aspect
@Component
@Slf4j
public class LoggingAspect {

    @Pointcut("(execution(* org.example.driverandfleetmanagementapp.controller.*.*(..)) || " +
            "execution(* org.example.driverandfleetmanagementapp.service.*.*(..)) || " +
            "execution(* org.example.driverandfleetmanagementapp.config.*.*(..)))")
    public void applicationPackagePointcut() {
    }


    @Pointcut("execution(* org.example.driverandfleetmanagementapp.exception.*.*(..))")
    public void exceptionPackagePointcut() {
    }


    @Before("applicationPackagePointcut()")
    public void logBefore(JoinPoint joinPoint) {
        String className = joinPoint.getTarget().getClass().getSimpleName();
        String methodName = joinPoint.getSignature().getName();
        String args = Arrays.toString(joinPoint.getArgs());

        log.info("Starting {} in class {}", methodName, className);
        log.debug("Method: {}.{} called with arguments: {}", className, methodName, args);
    }


    @After("applicationPackagePointcut()")
    public void logAfter(JoinPoint joinPoint) {
        String className = joinPoint.getTarget().getClass().getSimpleName();
        String methodName = joinPoint.getSignature().getName();

        log.info("Completed {} in class {}", methodName, className);
        log.debug("Method: {}.{} completed execution", className, methodName);
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
