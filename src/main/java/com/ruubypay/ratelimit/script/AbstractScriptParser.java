package com.ruubypay.ratelimit.script;

/**
 * 脚本解析接口。根据表达式获取真实的值
 * @author chenhaiyang
 */
public interface AbstractScriptParser {

    /**
     * 解析目标表达式
     * @param expressKey 表达式key
     * @param target 目标实例
     * @param arguments 参数
     * @return 返回解析结果
     */
    String getExpressValue(String expressKey, Object target, Object[] arguments);

    /**
     * 是否是满足条件的表达式
     * @param script 表达式
     * @return 返回布尔值
     */
    boolean isScript(String script);

    /**
     * 获取前缀
     * @param script 表达式
     * @return 返回表达式前缀，排除变量部分。
     */
    String getPrefix(String script);

}
