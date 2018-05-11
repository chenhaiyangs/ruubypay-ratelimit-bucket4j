package com.ruubypay.ratelimit.exception;

/**
 * 限流逻辑返回和aop拦截的返回类型不一致，抛出此异常
 * @author chenhaiyang
 */
public class WrongReturnTypeException extends RuntimeException{

    public WrongReturnTypeException(String msg){
        super(msg);
    }
}
