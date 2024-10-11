package com.tuling.tulingmall.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

/**
 * @author WangJinbiao
 * @date 2024/10/11 22：28
 */
@Aspect
@Component
@Slf4j
public class RoleCheckAspect {

    @Around("@annotation(com.tuling.tulingmall.annotation.DigitalAngel)")
    public Object checkRole(ProceedingJoinPoint joinPoint) throws Throwable {

        // 模拟角色检查逻辑，这里检查是否拥有 "DIGITAL_ANGEL" 角色
        boolean hasRole = Math.random() > 0.5;
        log.info("Aspect: 检查角色，是否拥有DIGITAL_ANGEL角色:{} " , hasRole);
        RoleContext.setHasRole(hasRole);
        try {
            return joinPoint.proceed();
        } finally {
            // 清理 ThreadLocal，避免内存泄漏
            RoleContext.clear();
        }
    }
}
