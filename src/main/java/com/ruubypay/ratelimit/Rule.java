package com.ruubypay.ratelimit;

import com.ruubypay.ratelimit.exception.InvalidConfigException;
import lombok.Data;

/**
 * 限流规则
 * @author chenhaiyang
 */
@Data
public class Rule {
    /**
     * 限流规则key
     */
    private String key;
    /**
     * 限流令牌数
     */
    private int fillrate;
    /**
     * 即每隔`seconds`秒向令牌桶中添加`fillrate`个令牌
     */
    private int seconds;
    /**
     * 令牌桶容量
     */
    private int capacity;


    public Rule(String key, String strConfig) throws InvalidConfigException {
        this.key = key;
        String[] configs = strConfig.split(",");
        try {
            fillrate = Integer.parseInt(configs[0]);
            seconds = Integer.parseInt(configs[1]);
            capacity = Integer.parseInt(configs[2]);
        } catch (Throwable e) {
            throw new InvalidConfigException(String.format("invalid config: [%s]", strConfig));
        }
    }
}
