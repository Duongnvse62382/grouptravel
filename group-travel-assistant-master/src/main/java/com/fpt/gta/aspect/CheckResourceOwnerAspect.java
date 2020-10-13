package com.fpt.gta.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class CheckResourceOwnerAspect {

    @Pointcut("@annotation(com.fpt.gta.annotation.CheckResourceOwner)")
    public void checkResourceOwnerMethod() throws Throwable {
    }

    @Before("checkResourceOwnerMethod()")
    public void logMethod(JoinPoint jp) {
        Object[] args = jp.getArgs();

    }

}
