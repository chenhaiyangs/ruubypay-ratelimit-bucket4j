package com.ruubypay.ratelimit.exception;

/**
 * 配置key不存在异常
 * @author chenhaiyang
 */
public class ConfigKeyNotExistsException extends RuntimeException{
    public ConfigKeyNotExistsException(String message) {
        super(message);
    }
}
