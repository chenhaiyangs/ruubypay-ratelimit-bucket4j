package com.ruubypay.ratelimit.aop;

import com.ruubypay.ratelimit.RateLimitHandler;
import com.ruubypay.ratelimit.annotation.RateLimit;
import com.ruubypay.ratelimit.aop.proxy.aspectj.AspectjRateLimitProxyChain;
import org.aspectj.lang.ProceedingJoinPoint;

/**
 * 使用aspectj 实现AOP拦截
 * 注意，要拦截的类的要拦截的方法不能有重名方法
 * @author chenhaiyang
 */
public class AspectjAopInterceptor {
    /**
     * 处理限流逻辑的核心实现
     */
    private final RateLimitHandler rateLimitHandler;

    public AspectjAopInterceptor(RateLimitHandler rateLimitHandler) {
        this.rateLimitHandler = rateLimitHandler;
    }

    /**
     * 处理方法
     * @param aopProxyChain 切点
     * @param rateLimit 拦截到的注解
     * @return 返回执行结果
     * @throws Throwable 抛出异常
     */
    public Object proceed(ProceedingJoinPoint aopProxyChain, RateLimit rateLimit) throws Throwable {
        return rateLimitHandler.proceed(new AspectjRateLimitProxyChain(aopProxyChain),rateLimit);
    }

}
