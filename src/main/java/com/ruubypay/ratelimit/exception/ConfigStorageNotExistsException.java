package com.ruubypay.ratelimit.exception;

/**
 * 配置存储类找不到时抛异常
 * @author chenhaiyang
 */
public class ConfigStorageNotExistsException extends RuntimeException{

    public ConfigStorageNotExistsException(String msg){
        super(msg);
    }
}
