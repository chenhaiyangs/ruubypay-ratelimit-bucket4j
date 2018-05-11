package com.ruubypay.ratelimit.annotation;

import java.lang.annotation.*;

/**
 * 限流的每个key
 * @author chenhaiyang
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Inherited
@Documented
public @interface RateLimitKey {
    /**
     * 要限流的key
     * @return 返回限流key
     */
    String key();

    /**
     * 处理该限流策略的实现类
     * @return 限流逻辑实现类
     */
    Class clazz();
}
