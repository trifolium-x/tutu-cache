package co.tunan.tucache.autoconfigure.configure.cache;

import co.tunan.tucache.autoconfigure.configure.TuCacheCondition;
import co.tunan.tucache.core.cache.TuCacheService;
import co.tunan.tucache.core.cache.impl.RedisCacheService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;

/**
 * @title: RedisCacheServiceConfigure
 * @author: trifolium.wang
 * @date: 2023/9/19
 * @modified :
 */
@Conditional(TuCacheCondition.class)
@ConditionalOnClass(name = "org.springframework.data.redis.core.RedisTemplate")
@ConditionalOnBean(org.springframework.data.redis.core.RedisTemplate.class)
public class RedisCacheServiceConfigure {

    public RedisCacheServiceConfigure() {
        System.out.println("注入了...RedisCacheServiceConfigure");
    }

    @Bean("redisTuCacheService")
    public TuCacheService redisTuCacheService(org.springframework.data.redis.core.RedisTemplate redisTemplate) {

        System.out.println("redis");
        RedisCacheService redisCacheService = new RedisCacheService();

        redisCacheService.setRedisTemplate(redisTemplate);

        return redisCacheService;
    }
}
