package co.tunan.tucache.autoconfigure.configure.cache;

import co.tunan.tucache.autoconfigure.configure.TuCacheCondition;
import co.tunan.tucache.core.cache.TuCacheService;
import co.tunan.tucache.core.cache.impl.CaffeineCacheService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;

/**
 * @title: CaffeineCacheServiceConfigure
 * @author: trifolium.wang
 * @date: 2023/9/19
 * @modified :
 */
@Conditional(TuCacheCondition.class)
@ConditionalOnClass(name = "com.github.benmanes.caffeine.cache.Caffeine")
public class CaffeineCacheServiceConfigure {

    public CaffeineCacheServiceConfigure(){
        System.out.println("注入了...CaffeineCacheServiceConfigure");
    }

    @Bean("caffeineTuCacheService")
    public TuCacheService caffeineTuCacheService() {
        System.out.println("caff");
        return new CaffeineCacheService();
    }
}
