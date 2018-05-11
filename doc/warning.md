### idea spring配置文件报错的问题

配置为以下的简化版本时
```xml
<ratelimit:interceptor /> 
```
@annotation(rateLimit) 这段配置会报错，但本身不影响启动。属于IDEA的bug。
```xml
       
    <aop:pointcut id="ratelimitPointCut"
        expression="execution(* com.ruubypay.miss.pushcenter.impl.*.*(..)) &amp;&amp;@annotation(rateLimit)"/>
             
```
如果想抑制报错。可以将 `<ratelimit:interceptor />`改为如下配置。实现的效果是等同的。
```xml
       
    <bean id="rateInterceptor" class="com.ruubypay.ratelimit.aop.AspectjAopInterceptor">
            <constructor-arg ref="rateLimitHandler"/>
    </bean>
             
```

### jar包冲突的问题

本限流框架依赖的两个库，都需要以下两个依赖为2.11.1的版本。<br/>
在一些其他的框架中。比如，config-toolkit,dubbox。都依赖了这两个库，但版本不一致会导致程序无法正常启动。<br/>
需要在引用低版本的框架gav中做好依赖排除，项目共同引用最新版本。<br/>
```xml
    <exclusion>
        <groupId>org.apache.curator</groupId>
        <artifactId>curator-framework</artifactId>
    </exclusion>
    <exclusion>
        <groupId>org.apache.curator</groupId>
        <artifactId>curator-client</artifactId>
    </exclusion>
```
    