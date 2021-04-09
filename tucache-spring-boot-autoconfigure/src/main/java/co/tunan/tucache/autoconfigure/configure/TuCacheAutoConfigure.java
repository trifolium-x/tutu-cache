package co.tunan.tucache.autoconfigure.configure;

import co.tunan.tucache.core.TuCacheBean;
import co.tunan.tucache.core.cache.TuCacheService;
import co.tunan.tucache.core.cache.impl.RedisCacheService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.cache.CacheAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Objects;

/**
 * 这个配置类没有被用到的原因：
 * 哪位大神帮我调试一下，这个类现在的问题，是redisTemplate总是在public TuCacheService tuCacheService()之后初始化，
 * 用尽了各种办法，找不到为什么RedisAutoConfiguration中的redisTemplate为什么总是比我这个靠后初始化。
 * 现在直接使用beanFactor的getBean获取的时候依然是直接报错redisTemplate初始化错误，而不是找不到bean。
 * 很奇怪的问题。
 * 暂时使用Supplier的方式让组件使用redisTemplate
 */

/**
 * @author wangxudong
 */
@Configuration
@ConditionalOnClass({TuCacheBean.class})
@AutoConfigureAfter({CacheAutoConfiguration.class})
@EnableConfigurationProperties(TuCacheConfigure.class)
@ConditionalOnProperty(prefix = "tucache", name = "enable", havingValue = "true", matchIfMissing = true)
public class TuCacheAutoConfigure implements BeanFactoryAware {

    private static final Logger log = LoggerFactory.getLogger(TuCacheAutoConfigure.class);
    private BeanFactory beanFactory;

    @Bean
    @ConditionalOnMissingBean
    public TuCacheBean tuCacheBean(TuCacheConfigure tuCacheConfigure, ObjectProvider<TuCacheService> tuCacheServices) {

        TuCacheService tuCacheService = tuCacheServices.getIfAvailable();

        TuCacheBean tuCacheBean = new TuCacheBean(tuCacheService);

        log.info("tucache is initialize.");

        tuCacheBean.setTuCacheProfiles(tuCacheConfigure.getProfiles());

        return tuCacheBean;
    }

    @Bean
    @ConditionalOnClass(name = "org.springframework.data.redis.core.RedisTemplate")
    @ConditionalOnMissingBean(TuCacheService.class)
    public TuCacheService tuCacheService() {

        // 我们用beanFactory来获取，因为可能出现用户不会依赖redisTemplate的情况，或者使用其他缓存
        //RedisTemplate redisTemplate = beanFactory.getBean("redisTemplate", RedisTemplate.class);
        log.info("use redis tucache service.");
        RedisCacheService redisCacheService = new RedisCacheService();

        redisCacheService.setRedisTemplateSupplier(() -> beanFactory.getBean("redisTemplate"));

        return redisCacheService;
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {

        this.beanFactory = beanFactory;
    }

}
