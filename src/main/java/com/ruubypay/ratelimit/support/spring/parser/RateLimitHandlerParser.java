package com.ruubypay.ratelimit.support.spring.parser;

import com.ruubypay.ratelimit.RateLimitHandler;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.AbstractSingleBeanDefinitionParser;
import org.w3c.dom.Element;

/**
 * 处理handler标签的spring语义解析器
 * @author chenhaiyang
 */
public class RateLimitHandlerParser extends AbstractSingleBeanDefinitionParser {

    /**
     * 配置中心config-groupBean的引用
     */
    private static final String CONFIG_GROUP_REF="config-group-ref";
    /**
     * 脚本解析器
     */
    private static final String SCRIPT_PARSER_REF="script-parser";

    @Override
    protected Class<?> getBeanClass(Element element) {
        return RateLimitHandler.class;
    }

    @Override
    protected void doParse(Element element, BeanDefinitionBuilder builder) {

        String configGroupRef = element.getAttribute(CONFIG_GROUP_REF);
        builder.addConstructorArgReference(configGroupRef);
        String scriptParser = element.getAttribute(SCRIPT_PARSER_REF);
        if(StringUtils.isNotBlank(scriptParser)){
            builder.addConstructorArgReference(scriptParser);
        }
    }
}
