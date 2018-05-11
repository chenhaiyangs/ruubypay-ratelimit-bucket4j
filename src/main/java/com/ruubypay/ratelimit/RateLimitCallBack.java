package com.ruubypay.ratelimit;

/**
 * 限流回调。当限流成功执行以后。需要将流量限制住，自定义返回
 * 使用者需要实现该接口编写自己的返回逻辑
 * @author chenhaiyang
 */
public interface RateLimitCallBack <R>{
    /**
     * 限流的实现拦截返回
     * @param args 入参。要拦截的aop对象的入参
     * @param rateLimitKey 限流框架解析spel后的真实的key
     * @return 出参。要求：要和AOP拦截的对象出参类型保持一致,否则会抛异常
     */
    R rateLimitReturn(Object[] args,String rateLimitKey);
}
