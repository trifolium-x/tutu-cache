![tutu-cache](https://socialify.git.ci/trifolium-x/tutu-cache/image?description=1&font=Inter&forks=1&issues=1&language=1&name=1&owner=1&pattern=Circuit%20Board&pulls=1&stargazers=1&theme=Light)
tutu-cache 是一个简单易用的Spring缓存注解。
<br/>
使用tutu-cache注解来代替@Cacheable和@CacheEvict等注解

[![GitHub license](https://img.shields.io/github/license/trifolium-x/tutu-cache)](https://github.com/trifolium-x/tutu-cache/blob/master/LICENSE)
[![RELEASE](https://img.shields.io/badge/RELEASE-1.0.4.RELEASE-blue)](https://github.com/trifolium-x/tutu-cache/releases/tag/1.0.4.RELEASE)

### Version
* 1.0.4.RELEASE
* 1.0.3.RELEASE
* 1.0.2.RELEASE
* 1.0.1.RELEASE
* 1.0.1
* 1.0.0

### 参考文档
   [tu-cache文档 https://doc.tucache.tunan.co/](https://doc.tucache.tunan.co/)
### 🥳Quick Start
1. 在springBoot中的使用
    * 引入jar依赖包
      ```xml
      <dependencies>
        <dependency>
            <groupId>co.tunan.tucache</groupId>
            <artifactId>tucache-spring-boot-starter</artifactId>
            <version>1.0.4.RELEASE</version>
        </dependency>
        <!-- 可选，建议使用redis,如有没redis依赖默认使用本地缓存 -->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-data-redis</artifactId>
        </dependency>
      </dependencies>
      ```
    * 在Configure类中注册javaBean redisTemplate或者使用默认的redisTemplate，必须开启aspectj的aop功能(默认是开启的)
      ```java
      @Bean(name = "redisTemplate")
      public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory redisConnectionFactory) {
            RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
            redisTemplate.setKeySerializer(new StringRedisSerializer());
            redisTemplate.setValueSerializer(new GenericJackson2JsonRedisSerializer());
            redisTemplate.setConnectionFactory(redisConnectionFactory);
      
            return redisTemplate;
      }
      ```
### 使用tu-cache
1. 使用tu-cache对service中的方法返回的数据进行缓存
    ```java
    @TuCache("test_service:getList")
    public List<String> getList(){
        return Arrays.asList("tu","nan");
    }
    ```
2. 使用tu-cache删除缓存中的数据
    ```java
    @TuCacheClear("test_service:getList")
    public void delList(){
    }
    ```
3. @TuCache参数
    * `String key() default ""` 缓存的字符串格式key,支持spEl表达式(使用#{}包裹spEl表达式)，默认值为方法签名
    * `long expire() default -1` 缓存的过期时间，单位(秒),默认永不过期. (**在1.0.4.RELEASE以上版本中建议使用 `timeout`**)
    * `boolean resetExpire() default false` 每次获取数据是否重置过期时间.
    * `TimeUnit timeUnit() default TimeUnit.SECONDS` 缓存的时间单位.
    * `String condition() default "true"` 扩展的条件过滤，值为spEl表达式(直接编写表达式不需要使用#{}方式声明为spEl)
    * 样例:
        ```java
        @TuCache(key="test_service:getList:#{#endStr}", timeout = 10, timeUnit=TimeUnit.SECONDS)
        public List<String> getList(String endStr){
            return Arrays.asList("tu","nan",endStr);
        }
        
        // 如果需要当前对象的的方法
        @TuCache(key="test_service:getList:#{#this.endStr()}", timeout = 120)
        public List<String> getList(){
            return Arrays.asList("tu","nan",endStr());
        }
        
        // 使用springBean, (使用安全访问符号?.，可以规避null错误，具体用法请查看spEl表达式)
        @TuCache(key="test_service:getList:#{@springBean.endStr()}", timeout = 120)
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
    * condition要求spEL返回一个boolean类型的值，例如：
      * condition = "#param.startsWith('a')"
      * condition = "false"

### 版本对应的spring基础环境版本
* 建议springBoot版本在2.1.x-2.7.x,目前兼容springBoot3
* tucache 1.0.4.RELEASE ----- spring 5.3.29 ----- springBoot版本2.6.17
* tucache 1.0.3.RELEASE ----- spring 5.3.15 ----- springBoot版本2.6.3
* tucache 1.0.2.RELEASE ----- spring 5.1.3.RELEASE ----- springBoot版本2.1.1.RELEASE
* tucache 1.0.1.RELEASE ----- spring 5.1.3.RELEASE ----- springBoot版本2.1.1.RELEASE
### 个性化设置
* tutu-cache默认提供了 RedisTuCacheService,如果用户使用的缓存是redis并配置了redisTemplate的bean则自动使用该默认缓存服务。
* 用户使用其他缓存，则需要自定义TuCacheService，实现该接口并注入到TuCacheBean中
* 在SpringBoot中在Configure类中配置相应的bean自动使用自定义的bean
* 如果用户需要每个缓存前面添加同意的keyPrefix，TuCacheBean的prefixKey参数
* springBoot中配置
    ```yaml
    tucache:
      enabled: true
      cache-type: redis
      profiles:
        cache-prefix: "my_tu_key_test:"
        # ...
    ```
* springMVC中注入到TuCacheBean
    ```xml
    <bean id="tuCacheProfiles" class="co.tunan.tucache.core.config.TuCacheProfiles">
        <property name="cachePrefix" value="test_tucache_prefixkey:" />
    </bean>
    ```
    ```xml
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
  
#### QQ 交流群: 76131683
#### 希望更多的开发者参与
☕️[请我喝一杯咖啡]
* *USDT address:`0xE8DF0c045714976c1c67fFc9aBb5538625d5EeDE`*
* *network: BNB Smart Chain(BEP20),Polygon,Ethereum(ERC20)*

### [感谢JetBrains提供的免费授权](https://jb.gg/OpenSource)
<a href="https://jb.gg/OpenSource">
<svg width="72px" fill="none" viewBox="0 0 64 64" class="_siteLogo__image_3fer6 _siteLogo__image_il5wq"><defs><linearGradient id="__WEBTEAM_UI_SITE_HEADER_LOGO_ID__0" x1="1" x2="62.75" y1="63" y2="1.25" gradientUnits="userSpaceOnUse"><stop stop-color="#FF9419"></stop><stop offset="0.43" stop-color="#FF021D"></stop><stop offset="0.99" stop-color="#E600FF"></stop></linearGradient></defs><path fill="url(#__WEBTEAM_UI_SITE_HEADER_LOGO_ID__0)" d="M22.14 0H59c2.76 0 5 2.24 5 5v36.86c0 2.65-1.055 5.195-2.93 7.07L48.93 61.07A10.003 10.003 0 0 1 41.86 64H5c-2.76 0-5-2.24-5-5V22.14c0-2.65 1.055-5.195 2.93-7.07L15.07 2.93A10.003 10.003 0 0 1 22.14 0Z"></path><path fill="#000" d="M52 12H6v46h46V12Z"></path><g fill="#fff" class="jetbrains-simple_svg__letters-to-show-hovered"><path d="M11.92 21.49a.87.87 0 0 0 .1-.42V16h1.635v5.19c0 .43-.09.815-.275 1.145-.185.33-.45.585-.785.77-.34.185-.725.275-1.16.275H10v-1.505h1.22c.16 0 .3-.035.42-.1s.215-.16.28-.285ZM10 51h16v3H10v-3Zm6.595-30.63h3.475v-1.38h-3.475v-1.565h3.835V16h-5.425v7.38h5.53v-1.42h-3.94v-1.59Zm6.725-2.905h-2.19V16h6v1.465h-2.18v5.915h-1.63v-5.915Z"></path><path fill-rule="evenodd" d="M14.935 29.295a2.155 2.155 0 0 0-.4-.16 1.612 1.612 0 0 0 .94-.71 1.64 1.64 0 0 0 .23-.87c0-.375-.1-.71-.305-1.005a2.016 2.016 0 0 0-.86-.69c-.37-.165-.79-.25-1.26-.25H10v7.38h3.345c.495 0 .94-.085 1.325-.26.39-.175.69-.415.905-.725.215-.31.32-.66.32-1.06 0-.365-.085-.69-.255-.98a1.75 1.75 0 0 0-.705-.67Zm-3.35-2.445h1.45c.2 0 .38.035.53.11a.81.81 0 0 1 .36.31c.085.135.13.29.13.47s-.045.35-.13.49-.205.245-.36.32c-.155.075-.33.115-.53.115h-1.45V26.85Zm2.5 4.47a.91.91 0 0 1-.395.325c-.17.075-.365.115-.59.115h-1.515v-1.895H13.1c.22 0 .41.04.585.12.17.08.305.195.4.34.095.145.14.315.14.505s-.045.35-.14.49Zm8.375-2.14c-.225.355-.54.635-.95.83l-.005-.005c-.08.035-.16.07-.245.1l1.685 2.885H21.08l-1.495-2.69H18.52v2.69h-1.635v-7.38h3.23c.535 0 1.005.095 1.41.29.405.195.72.465.94.82.22.355.33.75.33 1.22s-.11.885-.335 1.24Zm-2.465-2.24h-1.47v2.045h1.47c.23 0 .425-.04.595-.125.17-.08.3-.2.39-.355.09-.155.135-.34.135-.545 0-.205-.045-.385-.135-.54a.897.897 0 0 0-.39-.355 1.305 1.305 0 0 0-.595-.125Zm5.745-1.325-2.635 7.38h1.66l.52-1.63h2.735l.56 1.63h1.63l-2.7-7.38h-1.77Zm-.085 4.515.85-2.605.1-.475.1.475.905 2.605h-1.955Z" clip-rule="evenodd"></path><path d="M30.95 25.615h1.685v7.38H30.95v-7.38Zm7.835 4.99-3.175-4.99h-1.59v7.38h1.49V28l3.16 4.995h1.61v-7.38h-1.495v4.99Zm7.28-1.595c.285.205.51.46.67.77h-.005c.16.31.24.645.24 1.01 0 .445-.12.845-.355 1.2s-.565.63-.99.83c-.425.2-.915.3-1.46.3-.545 0-1.03-.1-1.45-.295a2.378 2.378 0 0 1-.995-.82 2.169 2.169 0 0 1-.37-1.22h1.635c0 .19.055.36.155.505.1.145.24.255.425.335.18.08.395.12.62.12.225 0 .425-.035.59-.105a.91.91 0 0 0 .39-.3c.095-.13.14-.28.14-.445 0-.2-.065-.375-.19-.51s-.295-.23-.515-.285l-1.33-.275a2.436 2.436 0 0 1-.915-.395c-.26-.19-.465-.43-.61-.715a2.085 2.085 0 0 1-.215-.96c0-.44.11-.83.335-1.175.22-.345.535-.61.935-.805.4-.195.85-.29 1.37-.29.52 0 .98.095 1.38.28.405.185.72.445.945.775.225.33.345.71.355 1.14h-1.635c0-.16-.045-.3-.13-.425s-.205-.22-.36-.29c-.155-.07-.34-.105-.54-.105s-.375.035-.53.1a.822.822 0 0 0-.355.275.69.69 0 0 0-.125.415c0 .175.06.32.175.44.115.12.275.2.47.245l1.255.26c.37.07.7.21.99.415Z"></path></g></svg>
</a>