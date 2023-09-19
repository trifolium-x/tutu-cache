package co.tunan.tucache.autoconfigure.configure.cache;

import co.tunan.tucache.core.cache.TuCacheService;
import co.tunan.tucache.core.cache.impl.RedisCacheService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;

/**
 * @title: RedisCacheServiceConfigure
 * @author: trifolium.wang
 * @date: 2023/9/19
 * @modified :
 */
@ConditionalOnClass(name = "org.springframework.data.redis.core.RedisTemplate")
public class RedisCacheServiceConfigure {

    public RedisCacheServiceConfigure(){
        System.out.println("初始化了：RedisCacheServiceConfigure");
    }

    @Bean("redisTuCacheService")
    public TuCacheService redisTuCacheService(org.springframework.data.redis.core.RedisTemplate redisTemplate) {

        RedisCacheService redisCacheService = new RedisCacheService();

        redisCacheService.setRedisTemplate(redisTemplate);

        return redisCacheService;
    }
}
