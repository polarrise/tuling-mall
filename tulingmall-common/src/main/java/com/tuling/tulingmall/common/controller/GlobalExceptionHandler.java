package com.tuling.tulingmall.common.controller;

import com.tuling.tulingmall.common.api.CommonResult;
import com.tuling.tulingmall.common.api.ResultCode;
import com.tuling.tulingmall.common.exception.TulingMallException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ResponseBody
    @ExceptionHandler(value = Exception.class)
    public CommonResult<ResultCode> handle(Exception e) {
        log.info("系统异常:{}", e.getMessage());
        return CommonResult.failed(ResultCode.FAILED.getCode(),ResultCode.FAILED.getMessage());
    }

    @ResponseBody
    @ExceptionHandler(value = MethodArgumentNotValidException.class)
    public CommonResult<ResultCode> handle(MethodArgumentNotValidException e) {
        log.info("参数异常:{}", e.getMessage());
        return CommonResult.failed(ResultCode.VALIDATE_FAILED.getCode(),e.getBindingResult().getFieldError().getDefaultMessage());
    }

    @ResponseBody
    @ExceptionHandler(value = TulingMallException.class)
    public CommonResult<ResultCode> handle(TulingMallException e) {
        log.info("业务异常:{}", e.getMessage());
        return CommonResult.failed(e.getExceptionCode(),e.getMessage());
    }
}
