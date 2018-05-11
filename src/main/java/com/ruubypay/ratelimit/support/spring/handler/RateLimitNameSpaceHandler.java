package com.ruubypay.ratelimit.support.spring.handler;

import com.ruubypay.ratelimit.support.spring.parser.RateLimitHandlerParser;
import com.ruubypay.ratelimit.support.spring.parser.RateLimitInterceptorHandler;
import org.springframework.beans.factory.xml.NamespaceHandlerSupport;

/**
 * 增加对spring支持的命名空间处理器
 * @author chenhaiyang
 */
public class RateLimitNameSpaceHandler extends NamespaceHandlerSupport {

    /**
     * spring配置文件的标签限流实现类
     */
    private static final String TAG="handler";
    /**
     * 解析interceptor标签
     */
    private static final String INTERCEPTOR="interceptor";
    @Override
    public void init() {
        registerBeanDefinitionParser(TAG, new RateLimitHandlerParser());
        registerBeanDefinitionParser(INTERCEPTOR,new RateLimitInterceptorHandler());
    }
}
