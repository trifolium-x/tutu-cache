package co.tunan.tucache.autoconfigure.configure.cache;

import co.tunan.tucache.core.cache.TuCacheService;
import co.tunan.tucache.core.cache.impl.CaffeineCacheService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;

/**
 * @title: CaffeineCacheServiceConfigure
 * @author: trifolium.wang
 * @date: 2023/9/19
 * @modified :
 */
@Slf4j
@ConditionalOnClass(name = "com.github.benmanes.caffeine.cache.Caffeine")
public class CaffeineCacheServiceConfigure {

    public CaffeineCacheServiceConfigure() {
        System.out.println("初始化了：CaffeineCacheServiceConfigure");
    }

    @Bean("caffeineTuCacheService")
    public TuCacheService caffeineTuCacheService() {

        // TODO
        CaffeineCacheService caffeineCacheService = new CaffeineCacheService();

        return caffeineCacheService;
    }
}
