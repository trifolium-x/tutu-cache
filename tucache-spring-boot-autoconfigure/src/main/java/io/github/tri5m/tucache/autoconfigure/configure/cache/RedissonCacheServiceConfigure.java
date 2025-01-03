package io.github.tri5m.tucache.autoconfigure.configure.cache;

import io.github.tri5m.tucache.autoconfigure.configure.TuCacheCondition;
import io.github.tri5m.tucache.core.cache.TuCacheService;
import io.github.tri5m.tucache.core.cache.impl.RedissonCacheService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;

/**
 * @title: RedissonCacheServiceConfigure
 * @author: trifolium.wang
 * @date: 2023/9/19
 * @modified :
 */
@Slf4j
@Conditional(TuCacheCondition.class)
@ConditionalOnClass(name = "org.redisson.api.RedissonClient")
@ConditionalOnBean(org.redisson.api.RedissonClient.class)
public class RedissonCacheServiceConfigure {

    @Bean("redissonTuCacheService")
    public TuCacheService redissonTuCacheService(org.redisson.api.RedissonClient redissonClient) {

        log.debug("Injected with RedissonTuCacheService");
        return new RedissonCacheService(redissonClient);
    }
}
