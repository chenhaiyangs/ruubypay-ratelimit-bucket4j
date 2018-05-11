package com.ruubypay.ratelimit;

import java.util.concurrent.ConcurrentHashMap;

/**
 * 限流实现类的缓存，这样就不必每次new 实例
 * @author chenhaiyang
 */
class RateLimitCallBackStorage {

    private static ConcurrentHashMap<String,RateLimitCallBack> callbackStorage = new ConcurrentHashMap<>();

    /**
     * 向缓存里添加实现类
     * @param key 字符串key
     * @param value 实现类value
     */
    static void setImpl(String key,RateLimitCallBack value){
        callbackStorage.put(key,value);
    }

    /**
     * 根据key来获取具体的实现
     * @param key key
     * @return 返回限流实现类
     */
    static RateLimitCallBack getImplByKey(String key){
        return callbackStorage.get(key);
    }

}
