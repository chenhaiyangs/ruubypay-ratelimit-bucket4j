限流key可以与业务对象绑定也可以不与业务对象绑定。<br/>
如果key是一个普通的字符串，则表示不与对象绑定。<br/>
如果key是一个表达式字符串，例如，Spel表达式，Ognl表达式等，则表示该限流key与业务对象绑定。下面两个例子。<br/>

### 不与对象绑定的限流key
```java
@RateLimit({@RateLimitKey(key ="xxxxLimitKey",clazz=XxxxLimitResponse.class)})
    public ApiResponse<Record> getXxxxApiResponse(ApiRequest request){
        //业务代码
        ......
    }
```
key为xxxxLimitKey。此时，就表示该限流key不与业务对象绑定。是一个全局限流的策略
此时，配置中心的配置示例： <br/>
Property Key :xxxxxLimitKey    <br/> 
Property Value: 200,1,220 <br/>

### 与业务对象绑定的限流key
```java
@RateLimit({@RateLimitKey(key ="'RateLimitByUserId:'+#args[0].userId",clazz=XxxxLimitResponse.class)})
    public ApiResponse<Record> getXxxxApiResponse(ApiRequest request){
        //业务代码
        ......
    }
```
key为 'RateLimitByUserId:'+#args[0].userId 实际上是一个Spring的Spel表达式。'RateLimitByUserId:'是这个表达式的前缀（注意单引号）。 +#args[0].userId表示的是使用第一个入参的userId字段做变量 <br>

此种方式就能实现基于用户id，基于ip等各种维度的与业务对象相关的限流。<br>
此时，配置中心的配置是配置一个表达式前缀，示例spel表达式注意单引号！： <br/>
Property Key :'RateLimitByUserId:' <br/> 
Property Value: 200,1,220 <br/>

### 一个函数的多限流策略支持

```java
    @RateLimit({@RateLimitKey(key ="'RateLimitByUserId:'+#args[0].userId",clazz=A.class),@RateLimitKey(key ="xxxxLimitKey",clazz=B.class)})
      public ApiResponse<Record> getXxxxApiResponse(ApiRequest request){
          //业务代码
          ......
      }
```
一个函数支持多个限流策略key的绑定。配置方式如上。

### 自定义表达式解析器

项目默认的表达式解析器是Spring的Spel解析器。<br/>
如需自定义自己的解析器。请自行实现com.ruubypay.ratelimit.script.AbstractScriptParser的接口进行扩展。<br>
并通过以下的配置方式注册自己的表达式解析器。script-parser标签 <br>
```xml
   
        <!-- 
            config-group-ref 是限流配置组的引用。
            例如。配置了<config:group id="limitGroup" node="rate-limit" />
            代表配置中心有一个rate-limit分组存放限流策略的配置。该组配置的限流策略可以动态加载，实时生效
         -->
        <ratelimit:handler config-group-ref="limitGroup" script-parser="myScriptParser"/>
        
```     
实现三个方法
```java
    /**
     * 解析目标表达式
     * @param expressKey 表达式key
     * @param target 目标实例
     * @param arguments 参数
     * @return 返回解析结果
     */
    String getExpressValue(String expressKey, Object target, Object[] arguments);

    /**
     * 是否是满足条件的表达式（是，代表是一个可解析的表达式，否，代表是一个普通字符串）
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
    
```
