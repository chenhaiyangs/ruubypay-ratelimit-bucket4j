package com.ruubypay.ratelimit;

import com.dangdang.config.service.GeneralConfigGroup;
import com.ruubypay.ratelimit.annotation.RateLimit;
import com.ruubypay.ratelimit.annotation.RateLimitKey;
import com.ruubypay.ratelimit.aop.proxy.RateLimitProxyChain;
import com.ruubypay.ratelimit.configtoolkit.RefreshableRuleStorage;
import com.ruubypay.ratelimit.exception.WrongReturnTypeException;
import com.ruubypay.ratelimit.script.AbstractScriptParser;
import com.ruubypay.ratelimit.script.SpelScriptParser;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Method;


/**
 * 处理限流逻辑的核心接口实现类
 * @author chenhaiyang
 */
@Slf4j
public class RateLimitHandler {
    /**
     * 标签语法解析器
     */
    private AbstractScriptParser parser;
    /**
     * 限流实现
     */
    private RateLimitImpl rateLimitImpl;

    /**
     * 使用默认的spel解析器实例化限流实现
     * @param group 配置组
     */
    public RateLimitHandler(GeneralConfigGroup group){
        this.parser = new SpelScriptParser();
        rateLimitImpl = new RateLimitImpl(new RefreshableRuleStorage(group));
    }

    /**
     * 使用自定义的解析器实例化限流实现
     * @param group 配置组
     * @param parser 解析器实现类
     */
    public RateLimitHandler(GeneralConfigGroup group,AbstractScriptParser parser){
        this.parser= parser;
        rateLimitImpl = new RateLimitImpl(new RefreshableRuleStorage(group));
    }

    /**
     * 执行限流逻辑
     * @param rateLimitProxyChain 拦截到的切面
     * @param rateLimit 限流注解
     * @return 返回执行结果
     */
    public Object proceed(RateLimitProxyChain rateLimitProxyChain, RateLimit rateLimit) throws Throwable {

        Object[] arguments=rateLimitProxyChain.getArgs();
        Object target = rateLimitProxyChain.getTarget();
        Method method = rateLimitProxyChain.getMethod();

        log.info("RateLimiter.proceed->{}.{},keys->{} " , target.getClass().getName(), method.getName(),rateLimit.value());

        try{
            RateLimitKey[] keys = rateLimit.value();
            for(RateLimitKey key :keys){
                String spelKey = key.key();
                Class<?> clazz = key.clazz();
                String trueKey = parser.getExpressValue(spelKey,target,arguments);
                if(StringUtils.isNotBlank(spelKey)&& !doRateLimit(spelKey,trueKey)){
                    return responseLimit(clazz,arguments,trueKey,method);
                }
            }
        }catch (Exception e){
            log.error("rateLimitError [{}.{}] ",target.getClass().getName(), method.getName(),e);
        }
        return rateLimitProxyChain.doProxyChain(arguments);
    }

    /**
     * 执行限流逻辑
     * @param spelKey spelKey spel表达式。
     * @param trueKey 经过spel表达式解析后的key
     * @return 返回限流结果，false表示该请求被限制
     */
    private boolean doRateLimit(String spelKey,String trueKey) {

        if(parser.isScript(spelKey)){
           String prefix = parser.getPrefix(spelKey);
           return rateLimitImpl.consumerToken(prefix,trueKey);
        }
       return rateLimitImpl.consumerToken(spelKey,trueKey);
    }

    /**
     * 如果一个请求被限制住，则需要服务降级返回
     * @param clazz 限流对应的类
     * @param arguments 参数
     * @param trueKey 经过spel函数解析后的key
     * @param method 拦截的方法
     * @return 返回服务降级对象
     */
    private Object responseLimit(Class<?> clazz, Object[] arguments, String trueKey,Method method) throws IllegalAccessException, InstantiationException {

        String className = clazz.getName();
        RateLimitCallBack<?> limitCallBack = RateLimitCallBackStorage.getImplByKey(className);
        if(limitCallBack==null){
            limitCallBack = (RateLimitCallBack) clazz.newInstance();
            RateLimitCallBackStorage.setImpl(className,limitCallBack);
        }
        Object result = limitCallBack.rateLimitReturn(arguments,trueKey);
        Class<?> returnClass = method.getReturnType();
        if(result.getClass()!=returnClass &&
                !returnClass.isAssignableFrom(result.getClass())){
            String msg = String.format("wrong return type of method. methodName: [%s] required: [%s], input: [%s]",method.getName(),returnClass,result.getClass());
            throw new WrongReturnTypeException(msg);
        }
        return result;
    }
}
