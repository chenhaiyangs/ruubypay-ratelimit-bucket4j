package com.ruubypay.ratelimit.annotation;

import java.lang.annotation.*;

/**
 * 限流注解，可以配置多个限流策略
 * @author chenhaiyang
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Inherited
@Documented
public @interface RateLimit {
    /**
     * 配置每一个的限流策略,
     * @return 限流策略注解
     */
    RateLimitKey[] value();

}
