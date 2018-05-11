package com.ruubypay.ratelimit.aop.proxy.aspectj;

import com.ruubypay.ratelimit.aop.proxy.RateLimitProxyChain;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.reflect.MethodSignature;

import java.lang.reflect.Method;

/**
 * aspjectj声明的aop切面实现类
 * @author chenhaiyang
 */
public class AspectjRateLimitProxyChain implements RateLimitProxyChain {
    /**
     * 切点
     */
    private final ProceedingJoinPoint joinPoint;
    /**
     * 方法
     */
    private Method method;

    public AspectjRateLimitProxyChain(ProceedingJoinPoint joinPoint) {
        this.joinPoint=joinPoint;

    }

    /**
     * 获取拦截的方法的参数列表
     * @return 返回参数列表
     */
    @Override
    public Object[] getArgs() {
        return joinPoint.getArgs();
    }

    /**
     * 获取要拦截的目标实例
     * @return 返回目标实例
     */
    @Override
    public Object getTarget() {
        return joinPoint.getTarget();
    }

    /**
     * 获取拦截的方法
     * @return 获取拦截的方法
     */
    @Override
    public Method getMethod() {
        if(null == method) {
            Signature signature=joinPoint.getSignature();
            MethodSignature methodSignature=(MethodSignature)signature;
            this.method=methodSignature.getMethod();
        }
        return method;
    }

    /**
     * 执行方法
     * @param arguments 参数
     * @return 返回对象
     * @throws Throwable 抛出异常
     */
    @Override
    public Object doProxyChain(Object[] arguments) throws Throwable {
        return joinPoint.proceed(arguments);
    }
}
