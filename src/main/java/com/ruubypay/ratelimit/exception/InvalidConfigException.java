package com.ruubypay.ratelimit.exception;

/**
 * 配置无效时抛出的异常，当无法正确解析配置中心的配置时抛出此异常
 * @author chenhaiyang
 */
public class InvalidConfigException extends RuntimeException{

    public InvalidConfigException(String message) {
        super(message);
    }
}
