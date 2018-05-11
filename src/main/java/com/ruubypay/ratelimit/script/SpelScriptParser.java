package com.ruubypay.ratelimit.script;

import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import java.util.concurrent.ConcurrentHashMap;

/**
 * spel语法解析器
 * @author chenhaiyang
 */
public class SpelScriptParser implements AbstractScriptParser{
    /**
     * spel表达式的关键符号
     */
    private static final String SPEL_SEPARATE="#";
    /**
     * spel表达式的关键符号
     */
    private static final String SEPEL_SEPARATE_="'";
    /**
     * spel语法之目标实例
     */
    private static final String TARGET="target";
    /**
     * spel语法之参数名
     */
    private static final String ARGS="args";

    /**
     * SPEL表达式解析器
     */
    private final ExpressionParser parser=new SpelExpressionParser();
    /**
     * 缓存表达式的map
     */
    private final ConcurrentHashMap<String, Expression> rateKeyCache=new ConcurrentHashMap<>();

    @Override
    public String getExpressValue(String getExpressValue, Object target, Object[] arguments){
        // 如果不是表达式，直接返回字符串
        if(!isScript(getExpressValue)){
            return getExpressValue;
        }

        StandardEvaluationContext context=new StandardEvaluationContext();

        context.setVariable(TARGET, target);
        context.setVariable(ARGS, arguments);

        Expression expression=rateKeyCache.get(getExpressValue);
        if(null == expression) {
            expression=parser.parseExpression(getExpressValue);
            rateKeyCache.put(getExpressValue, expression);
        }
        return expression.getValue(context,String.class);
    }

    @Override
    public boolean isScript(String script) {
        return script.contains(SPEL_SEPARATE) && script.contains(SEPEL_SEPARATE_);
    }

    /**
     * spel表达式，获取前缀
     * 例如：'getUserByPhone:'+#args[0].phoneNo，其表达式前缀为：'getUserByPhone:'
     * @param script 表达式
     * @return 返回表达式前缀
     */
    @Override
    public String getPrefix(String script) {
        String prefix=script;
        if(isScript(script)){
            prefix =script.substring(0,script.lastIndexOf(SEPEL_SEPARATE_)+1);
        }
        return prefix;
    }
}
