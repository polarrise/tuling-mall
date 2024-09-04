package com.tuling.tulingmall.common.exception;

public class TulingMallException extends RuntimeException{

    private long exceptionCode;

    public TulingMallException(Long exceptionCode,String message){
        super(message);
        this.exceptionCode = exceptionCode;
    }

    public TulingMallException(){super();}

    public long getExceptionCode() {
        return exceptionCode;
    }

    public void setExceptionCode(long exceptionCode) {
        this.exceptionCode = exceptionCode;
    }
}
