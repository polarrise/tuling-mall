package com.tuling.tulingmall.aspect;

import cn.hutool.json.JSON;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.tuling.tulingmall.annotation.ApiAnnotation;
import com.tuling.tulingmall.common.api.CommonResult;
import com.tuling.tulingmall.common.api.ResultCode;
import lombok.extern.apachecommons.CommonsLog;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;

@Aspect
@Component
public class ApiAspect {

    @Pointcut("@annotation(com.tuling.tulingmall.annotation.ApiAnnotation)")
    public void apiAnnotation(){

    }

    @Around("apiAnnotation()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable{
        Boolean flag = Boolean.FALSE;
        try {
            flag = this.checkApiAccess(joinPoint);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        if(!flag){
            return CommonResult.failed(ResultCode.FORBIDDEN);
        }
        return joinPoint.proceed(joinPoint.getArgs());
    }

    private Boolean checkApiAccess(JoinPoint joinPoint){
        Object[] args = joinPoint.getArgs();
        if(args == null || args.length == 0){
            return false;
        }

        JSONObject jsonObject = JSONUtil.parseObj(args[0]);
        Method method = ((MethodSignature) joinPoint.getSignature()).getMethod();
        if(method.isAnnotationPresent(ApiAnnotation.class)){
            HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
            // 请求URL
            String requestURI = request.getRequestURI();
            String systemCode = request.getParameter("systemCode");
            if(StringUtils.isEmpty(systemCode)){
                systemCode = jsonObject.get("systemCode").toString();
            }
            // 查询系统编码、加这个请求url的api配置表，判断是否有权限访问

            // 查的到记录返回true

            // 查不到记录返回false
            return false;
        }
        return true;
    }
}
