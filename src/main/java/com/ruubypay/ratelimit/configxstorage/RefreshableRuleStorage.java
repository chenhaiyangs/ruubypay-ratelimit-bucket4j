package com.ruubypay.ratelimit.configxstorage;

import com.ruubypay.framework.configx.AbstractGeneralConfigGroup;
import com.ruubypay.ratelimit.ConfigStorage;
import com.ruubypay.ratelimit.RateLimitImpl;
import com.ruubypay.ratelimit.Rule;
import com.ruubypay.ratelimit.configx.AbstractRefreshableBox;
import com.ruubypay.ratelimit.exception.ConfigKeyNotExistsException;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;

/**
 * configx的一个ConfigStorage的实现
 * @author chenhaiyang
 */
@Slf4j
public class RefreshableRuleStorage extends AbstractRefreshableBox<HashMap<String, Rule>> implements ConfigStorage{

    private RateLimitImpl callback;
    public RefreshableRuleStorage(AbstractGeneralConfigGroup node) {
        super(node);
    }

    /**
     * 获取规则
     * @param key 规则key
     * @return 规则
     */
    @Override
    public Rule getRule(String key) throws ConfigKeyNotExistsException {
        if (!this.getObj().containsKey(key)) {
            throw new ConfigKeyNotExistsException(String.format("config spelKey [%s] not exists", key));
        }
        return this.getObj().get(key);
    }

    /**
     * 设置回调
     * @param callback 回调对象
     */
    @Override
    public void setCallback(RateLimitImpl callback) {
        this.callback = callback;
    }

    /**
     * 当配置更新时，此方法被调用
     * @param node 配置node
     * @return 返回规则集合
     */
    @Override
    protected HashMap<String, Rule> doInit(AbstractGeneralConfigGroup node) {
        HashMap<String, Rule> temp = new HashMap<>(16);
        node.forEach((k, v) -> temp.put(k, new Rule(k, v)));
        log.info("refreshing rule storage, total rules: [{}]", temp.size());
        if (callback != null) {
            log.info("remove all existing buckets");
            callback.clear();
        }
        return temp;
    }
}
