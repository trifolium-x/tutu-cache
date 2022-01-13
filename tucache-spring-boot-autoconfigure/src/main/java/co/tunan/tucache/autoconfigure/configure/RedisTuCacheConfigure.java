package co.tunan.tucache.autoconfigure.configure;

import co.tunan.tucache.core.cache.TuCacheService;
import co.tunan.tucache.core.cache.impl.RedisCacheService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;

/**
 * tu-cache redis configuration
 *
 * @author wangxudong
 * @date 2020/08/28
 */
@Configuration
@ConditionalOnClass(name = "org.springframework.data.redis.core.RedisTemplate")
public class RedisTuCacheConfigure {

    private static final Logger log = LoggerFactory.getLogger(RedisTuCacheConfigure.class);

    @Bean
    @ConditionalOnMissingBean(TuCacheService.class)
    public TuCacheService tuCacheService(RedisTemplate redisTemplate) {

        log.info("use redis tu-cache service.");
        RedisCacheService redisCacheService = new RedisCacheService();

        redisCacheService.setRedisTemplate(redisTemplate);

        return redisCacheService;
    }
}
