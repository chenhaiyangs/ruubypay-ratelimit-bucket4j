package com.ruubypay.ratelimit;

import com.hazelcast.core.Hazelcast;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.ICacheManager;
import com.ruubypay.ratelimit.exception.ConfigStorageNotExistsException;
import io.github.bucket4j.*;
import io.github.bucket4j.grid.GridBucketState;
import io.github.bucket4j.grid.ProxyManager;
import io.github.bucket4j.grid.jcache.JCache;
import lombok.extern.slf4j.Slf4j;

import javax.cache.Cache;
import java.time.Duration;
import java.util.function.Supplier;

/**
 * 限流核心实现类
 * @author chenhaiyang
 */
@Slf4j
public class RateLimitImpl {

    private final Cache<String, GridBucketState> cache = init();
    private Cache<String, GridBucketState> init() {
        HazelcastInstance instance = Hazelcast.newHazelcastInstance();
        ICacheManager manager = instance.getCacheManager();
        return manager.getCache("buckets");
    }
    private final ProxyManager<String> buckets = Bucket4j.extension(JCache.class).proxyManagerForCache(cache);

    /**
     * 配置存储类
     */
    private ConfigStorage configStorage;
    RateLimitImpl(ConfigStorage configStorage){
        log.info("using config storage: [{}]", configStorage);
        this.configStorage = configStorage;
        this.configStorage.setCallback(this);
    }
    public RateLimitImpl(){

    }

    /**
     * 和配置中心集成的限流逻辑
     * @param prefix spelkey 原始的spelkey表达式的前缀。用于找寻限流配置
     * @param key 解析spel表达式以后的真实key
     * @return 返回限流结果
     */
     boolean consumerToken(String prefix,String key){
        if (configStorage == null) {
            throw new ConfigStorageNotExistsException("no ConfigStorage Instance be found!");
        }
        Rule rule = configStorage.getRule(prefix);
        return consumeToken(key, rule.getFillrate(), rule.getSeconds(), rule.getCapacity());
    }

    /**
     * 执行限流逻辑
     * @param key key（替换了spel表达式后的真实的key）
     * @param fillrate 桶容量
     * @param seconds 即每隔`seconds`秒向令牌桶中添加`fillrate`个令牌
     * @param capacity 令牌桶容量
     * @return 返回false表示令牌桶以满，执行限流
     */
    public boolean consumeToken(String key, int fillrate, int seconds, int capacity) {
        BucketConfiguration configuration = Bucket4j
                .configurationBuilder()
                .addLimit(Bandwidth.classic(capacity, Refill.smooth(fillrate, Duration.ofSeconds(seconds))))
                .buildConfiguration();

        Supplier<BucketConfiguration> supplier = () -> configuration;
        Bucket bucket = buckets.getProxy(key, supplier);

        ConsumptionProbe probe = bucket.tryConsumeAndReturnRemaining(1);
        if (probe.isConsumed()) {
            log.trace("Key: [{}], Token remains: [{}]", key, probe.getRemainingTokens());
        }
        return probe.isConsumed();
    }

    /**
     * 清除数据
     */
    public void clear() {
        cache.clear();
    }
}
