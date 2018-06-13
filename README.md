# RateLimit 请求限制工具

实现：springSpel + springAop + annotation + configx + bucket4j + jcache + zookeeper <br/>
亦可直接开箱即用，不强依赖 configx和Spring。在本框架中，configx工具只是作为一个热配置中心的实现，依靠他可以实现限流参数动态热加载。<br/>
用户也可自行实现com.ruubypay.ratelimit.ConfigStorage 去扩展自己的限流参数配置类实现。<br/>

### 快速接入：

一，导入依赖：
```xml
    <!-- 分布式限流 -->
    <dependency>
        <groupId>com.github.chenhaiyangs</groupId>
        <artifactId>ruubypay-ratelimit-bucket4j</artifactId>
        <version>1.1.0</version>
    </dependency>
```    
二，直接使用：
1. 在项目resources或任意classpath下添加hazelcast.xml配置文件，修改其中zookeeper相关设置。
2. 在项目中生成RateLimit单例，在每个请求前添加下列代码实现请求限制。

```java
    public ApiResponse<String> getXXX(@QueryParam("userId") String userId) {
        if (!rateLimit.consumeToken("getQrCodeLimitByUser:" + userId, 1, 1, 2)) {
            //参数第一项为桶的key，使用规则名称+对象信息作为key
            //直接返回错误信息
            return new ApiResponse<>(-998, "User RateLimit Exceed");
        }
        //还可以添加更多维度的检查，比如基于IP或其他条件
        if (!rateLimit.consumeToken("getQrCodeLimitByIp:" + ip, 5, 1, 10)) {
            //直接返回错误信息
            return new ApiResponse<>(-999, "Ip RateLimit Exceed");
        }
        //业务代码
        ...
    }
```

### API详解

    public boolean consumeToken(String key, int fillrate, int seconds, int capacity) 为实现限流策略的核心方法.
其中，`key` 指令牌桶的key，一个key对应一个令牌桶，在此使用 `规则名称+对象信息`作为令牌桶的key。<br/>
`key` 可以是和业务对象息息相关的。例如："getQrCodeLimitByUser:" + userId。保证的是对同一个用户访问频次的限流。<br/>
`key` 也可以和对象无关。例如："getOrderLimit"。这时，可能只是针对一个接口函数整体的限流。<br/>
`fillrate`、`seconds`和`capacity`是指令牌桶的填充策略，即每隔`seconds`秒向令牌桶中添加`fillrate`个令牌，令牌桶容量为`capacity`。<br/>
方法返回值为`boolean`类型，每次需要执行业务时要从桶中消费一个令牌，当令牌桶为空时方法会返回`false`，此时应拒绝执行相关业务。<br/>
假如设置将填充策略分别设置为 120,60,240，则意味着：<br/>
* 每60秒向令牌桶中添加120个令牌，为了保证限流平滑，实际令牌填充并不会按照60秒为单位执行，而是换算成每0.5秒向桶中添加1个令牌。因此下列配置是 完全等价 的，可以任意选择一个适合解读的比例进行配置。
 * 120,60,240
 * 600,300,240
 * 2,1,240
* 240指令牌桶容量为240个，即可以保存240个令牌，该设置是为了在基础限流的同时保证突发流量可以被桶内额外保存的令牌处理。（限流更灵活）

### 与configx和Spring框架集成。

实际开发中，一般不会硬编码限流功能。限流策略需要动态配置（与configx集成）也要充分考虑限流代码与业务代码的低耦合要求。因此本工具也提供了基于AOP+annotation的注解的实现。详细用法：<br/>
1. 在项目resources或任意classpath下添加hazelcast.xml配置文件，修改其中zookeeper相关设置。
2. 添加一个spring配置文件，并被spring容器加载
```xml
    <?xml version="1.0" encoding="UTF-8"?>
    <!--suppress ALL -->
    <beans xmlns="http://www.springframework.org/schema/beans"
           xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
           xmlns:ratelimit="http://www.ruubypay.com/schema/ratelimit"
           xmlns:aop="http://www.springframework.org/schema/aop"
           xsi:schemaLocation="http://www.springframework.org/schema/beans http://www.springframework.org/schema/beans/spring-beans.xsd
                               http://www.springframework.org/schema/aop http://www.springframework.org/schema/aop/spring-aop.xsd
                               http://www.ruubypay.com/schema/ratelimit http://www.ruubypay.com/schema/ratelimit/ratelimit.xsd">
    
        <!-- 
            config-group-ref 是限流配置组的引用。
            例如。配置了<config:group id="limitGroup" node="rate-limit" />
            代表配置中心有一个rate-limit分组存放限流策略的配置。该组配置的限流策略可以动态加载，实时生效
         -->
        <ratelimit:handler config-group-ref="limitGroup"/>
        
        <ratelimit:interceptor />
    
        <!-- aspectj织入 配置-->
        <aop:aspectj-autoproxy proxy-target-class="true"/>
        <aop:config proxy-target-class="true">
            <!-- 处理 @RateLimit AOP-->
            <aop:aspect ref="rateInterceptor">
                <aop:pointcut id="ratelimitPointCut"
                              expression="execution(* com.ruubypay.miss.business.impl.*.*(..)) &amp;&amp;@annotation(rateLimit)"/>
                <aop:around pointcut-ref="ratelimitPointCut" method="proceed"/>
            </aop:aspect>
        </aop:config>
    </beans> 
```  
expression="execution(* com.ruubypay.miss.business.impl.*.*(..)) &amp;&amp;@annotation(rateLimit)" 是一个AOP表达式 <br>
含义是拦截com.ruubypay.miss.business.impl包下的任意受spring容器管理的类的带有rateLimit注解的函数。<br>
com.ruubypay.miss.business.impl是一个Demo路径。可以是一个处理Http请求的Controller。<br>

3. 编写针对某个限流key的处理实现类
例如：
```java
    /**
     * 针对具体某个限流key编写的实现类
     * @author xxxx
     */
    public class XxxxLimitResponse implements RateLimitCallBack<ApiResponse<Xxxx>>{
    
        private static Logger logger = LoggerFactory.getLogger(XxxxLimitResponse.class);
    
        @Override
        public ApiResponse<Xxxx> rateLimitReturn(Object[] params, String key) {
            ApiRequest apiRequest = (ApiRequest) params[0];
            logger.info("限流器执行。请求参数：{} ,限流策略key:{}",apiRequest,key);
           
            ApiResponse<Record> response = new ApiResponse<>();
            response.setResCode("-998");
            response.setResData(null);
            response.setResMessage("RateLimit Exceed");
            return response;
        }
    }
```
4. 在要限流的方法上添加@RateLimit注解
```java
@RateLimit({@RateLimitKey(key ="xxxxLimitKey",clazz=XxxxLimitResponse.class)})
    public ApiResponse<Record> getXxxxApiResponse(ApiRequest request){
        //业务代码
        ......
    }
```
注解@Ratelimit接受一个策略List。List的元素为@RateLimitKey。key为限流key。clazz则为指定限流逻辑的实现类。<br/>
一个方法上可以生效多个限流key。
4. 在配置中心的限流策略分组，配置限流key的策略。
```
    在配置中心的限流策略分组中，配置限流策略。例如，key为xxxxLimitKey，value为fillrate,seconds,capacity 逗号间隔。
    配置中心调整key的限流速率，服务的限流速率会动态改变生效。
```
### [RateLimitKey注解中限流key语法详解](./doc/script.md)
### [注意事项](./doc/warning.md)
### [压力测试报告](./doc/compress.md)