package com.tuling.tulingmall.common.exception;

/**
 *
 * @author ：图灵学院
 * @version: V1.0
 * @slogan: 天下风云出我辈，一入代码岁月催
 * @description: 业务异常
 **/
public class BusinessException extends Exception {

    public BusinessException(){super();}

    public BusinessException(String message) {
        super(message);
    }

}
