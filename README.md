tutu-cache 是为了解决SpringCache缓存注解不够灵活的问题而做的SpringAop项目。
使用tutu-cache注解来代替@Cacheable和@CacheEvict等注解

[![GitHub license](https://img.shields.io/github/license/trifolium-wang/tutu-cache)](https://github.com/trifolium-wang/tutu-cache/blob/master/LICENSE)
[![GitHub issues](https://img.shields.io/github/issues/trifolium-wang/tutu-cache)](https://github.com/trifolium-wang/tutu-cache/issues)
[![RELEASE](https://img.shields.io/badge/RELEASE-1.0.1.RELEASE-blue)](https://github.com/trifolium-wang/tutu-cache/releases/tag/1.0.2.RELEASE)

### 版本
* 1.0.2.RELEASE
* 1.0.1.RELEASE
* 1.0.1 (公测版本)
* 1.0.0(内测) 
### 引入tutu-cache
1. 在springBoot中的使用
    * 引入jar依赖包
        ```xml
           <dependency>
               <groupId>co.tunan.tucache</groupId>
               <artifactId>tucache-spring-boot-starter</artifactId>
               <version>1.0.2.RELEASE</version>
           </dependency>
           <dependency>
               <groupId>org.springframework.boot</groupId>
               <artifactId>spring-boot-starter-data-redis</artifactId>
           </dependency>
           <!-- 或者其他缓存 -->
        ```
    * 在Configure类中注册javaBean redisTemplate或者使用默认的redisTemplate，必须开启aspectj的aop功能(默认是开启的)
      ```java
      @Bean(name = "redisTemplate")
      public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
            RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
            redisTemplate.setKeySerializer(new StringRedisSerializer());
      
            redisTemplate.setConnectionFactory(redisConnectionFactory);
      
            return redisTemplate;
      }
      ```
2. 在springMVC中的使用
    * 引入jar依赖包
        ```xml
        <dependency>
          <groupId>co.tunan.tucache</groupId>
          <artifactId>cache-core</artifactId>
          <version>1.0.2.RELEASE</version>
        </dependency>
        ```
    * 在applicationContent.xml中配置
        ```xml
        <!-- 如果使用的缓存是redis则注入默认实现的RedisCacheService -->
        <bean id="redisCacheService" class="co.tunan.tucache.core.cache.impl.RedisCacheService">
            <property name="redisTemplate" ref="redisTemplate" />
        </bean>
        
        <bean id="tuCacheAspect" class="co.tunan.tucache.core.aspect.TuCacheAspect">
            <property name="tuCacheService" ref="redisCacheService" />
        </bean>
        
        <tx:annotation-driven proxy-target-class="true"/>
        <!-- 使用cgLib代理 -->
        <!-- 注意项目中需要引入spring-aop和spring-data-redis的相关依赖 -->
        ```
### 使用tu-cache
1. 使用tu-cache对service中的方法返回的数据进行缓存
    ```java
    @TuCache("test_service:getList")
    public List<String> getList(){
        return Arrays.asList("tu","nan");
    }
    ```
1. 使用tu-cache删除缓存中的数据
    ```java
    @TuCacheClear("test_service:getList")
    public void delList(){
    }
    ```
3. @TuCache参数
    * `String key() default ""` 缓存的字符串格式key,支持spEl表达式(使用#{}包裹spEl表达式)，默认值为方法签名
    * `long expire() default -1` 缓存的过期时间，单位(秒),默认永不过期.
    * `boolean resetExpire() default false` 每次获取数据是否重置过期时间.
    * `TimeUnit timeUnit() default TimeUnit.SECONDS` 缓存的时间单位.
    * `String condition() default "true"` 扩展的条件过滤，值为spEl表达式(直接编写表达式不需要使用#{}方式声明为spEl)
    * 样例:
        ```java
        @TuCache(key="test_service:getList:#{#endStr}", expire = 10, timeUnit=TimeUnit.SECONDS)
        public List<String> getList(String endStr){
            return Arrays.asList("tu","nan",endStr);
        }
        
        // 如果需要当前对象的的方法
        @TuCache(key="test_service:getList:#{#this.endStr()}",expire = 120)
        public List<String> getList(){
            return Arrays.asList("tu","nan",endStr());
        }
        
        // 使用springBean, (使用安全访问符号?.，可以规避null错误，具体用法请查看spEl表达式)
        @TuCache(key="test_service:getList:#{@springBean.endStr()}",expire = 120)
        public List<String> springBeanGetList(){
            return Arrays.asList("tu","nan",springBean.endStr());
        }
        
        // 使用condition,当name的长度>=5时进行缓存
        @TuCache(key="test_service:getList:#{#name}", condition="#name.length() >= 5")
        public List<String> springBeanGetList(String name){
            return Arrays.asList("tu","nan",name);
        }
        
        public String endStr(){
          return "end";
        }
        ```
4. @TuCacheClear参数
    * `String[] key() default {}` 删除的key数组，支持spEl表达式(使用#{}包裹spEl表达式)
    * `String[] keys() default {}` 模糊删除的缓存key数组,支持spEl表达式(使用#{}包裹spEl表达式),对应redis中**deleteKeys**("test_service:")
    * `boolean async() default false` 是否异步删除，无需等待删除的结果
    * `String condition() default "true"` 扩展的条件过滤，值为spEl表达式(直接编写表达式不需要使用#{}方式声明为spEl)
    * 样例:
        ```java
        @TuCacheClear(key={"test_service:itemDetail:#{#id}"})
        public void deleteItem(Long id){
        }
        
        // 如果需要调用本地的方法
        @TuCacheClear(keys={"test_service:itemList:","test_service:itemDetail:#{#id}"}, async = true)
        public void deleteItem(Long id){
        }
        ```
    * _注意key和keys的区别_
5. condition 的用法

### 版本对应的spring框架
* tucache 1.0.1.RELEASE ----- spring 5.1.3.RELEASE+ ----- springBoot版本2.1.1.RELEASE+
* tucache 1.0.2.RELEASE ----- spring 5.1.3.RELEASE+ ----- springBoot版本2.1.1.RELEASE+
### 个性化配置
* tutu-cache默认提供了 RedisTuCacheService,如果用户使用的缓存是redis并配置了redisTemplate的bean则自动使用该默认缓存服务。
* 用户使用其他缓存，则需要自定义TuCacheService，实现该接口并注入到TuCacheBean中
* 在SpringBoot中在Configure类中配置相应的bean自动使用自定义的bean
* 如果用户需要每个缓存前面添加同意的keyPrefix，TuCacheBean的prefixKey参数
* springBoot中配置
    ```yaml
    tucache:
      enabled: true
      profiles:
        cache-prefix: "my_tu_key_test:"
    ```
* springMVC中注入到TuCacheBean
    ```xml
    <bean id="tuCacheProfiles" class="co.tunan.tucache.core.config.TuCacheProfiles">
        <property name="cachePrefix" value="test_tucache_prefixkey:" />
    </bean>
    <bean id="tuCacheBean" class="co.tunan.tucache.core.aspect.TuCacheAspect">
        <property name="tuCacheService" ref="redisCacheService" />
        <property name="tuCacheProfiles" ref="tuCacheProfiles" />
    </bean>
    ```
* 关于默认RedisTuCacheService的序列化问题，强烈建议使用对key使用String方式序列化
* 使用Json序列化配置样例如下:
    ```java
    @Bean(name = "redisTemplate")
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setHashKeySerializer(new StringRedisSerializer());
        redisTemplate.setHashValueSerializer(new GenericJackson2JsonRedisSerializer(createGenericObjectMapper()));
        redisTemplate.setValueSerializer(new GenericJackson2JsonRedisSerializer(createGenericObjectMapper()));
    
        redisTemplate.setConnectionFactory(redisConnectionFactory);
    
        return redisTemplate;
    }
    ```

## 友情连接
[OkHttps 超级优雅的 API 设计，且链式调用，让你顺滑到底！超级轻量，但性能卓越！](https://gitee.com/ejlchina-zhxu/okhttps)