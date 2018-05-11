package com.ruubypay.ratelimit;

/**
 * 设置配置存储的接口
 * @author chenhaiyang
 */
public interface ConfigStorage {
    /**
     * 获取规则
     * @param key 规则key
     * @return 返回Rule
     */
    Rule getRule(String key);

    /**
     * 设置回调。当ConfigStorage中的限流key有更新后
     * 需要调用 rateLimit.clear() 清空 cache
     * @param rateLimit 限流实现类
     */
    void setCallback(RateLimitImpl rateLimit);
}
