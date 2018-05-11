package com.ruubypay.ratelimit.support.spring.parser;

import com.ruubypay.ratelimit.aop.AspectjAopInterceptor;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.AbstractSingleBeanDefinitionParser;
import org.w3c.dom.Element;

/**
 * 处理Interceptor标签的语义解析器
 * @author chenhaiyang
 */
public class RateLimitInterceptorHandler extends AbstractSingleBeanDefinitionParser {

    /**
     * ratelimit-handler引用
     */
    private static final String HANDLER_REF="handler";

    @Override
    protected Class<?> getBeanClass(Element element) {
        return AspectjAopInterceptor.class;
    }

    @Override
    protected void doParse(Element element, BeanDefinitionBuilder builder) {

        String handlerBean = element.getAttribute(HANDLER_REF);
        builder.addConstructorArgReference(handlerBean);

    }
}
