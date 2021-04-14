package co.tunan.tucache.autoconfigure.configure;

import co.tunan.tucache.core.aspect.TuCacheAspect;
import co.tunan.tucache.core.cache.TuCacheService;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.cache.CacheAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * @author wangxudong
 */
@Configuration
@ConditionalOnClass({TuCacheService.class})
@AutoConfigureAfter({CacheAutoConfiguration.class})
@EnableConfigurationProperties(TuCacheConfigure.class)
@Import({RedisTuCacheConfigure.class})
@ConditionalOnProperty(prefix = "tucache", name = "enabled", havingValue = "true", matchIfMissing = true)
public class TuCacheAutoConfigure {

    @Bean
    public TuCacheAspect tuCacheAspect(TuCacheConfigure tuCacheConfigure, ObjectProvider<TuCacheService> tuCacheServices) {

        TuCacheService tuCacheService = tuCacheServices.getIfAvailable();

        TuCacheAspect tuCacheAspect = new TuCacheAspect();
        tuCacheAspect.setTuCacheProfiles(tuCacheConfigure.getProperties());
        tuCacheAspect.setTuCacheService(tuCacheService);

        return tuCacheAspect;
    }

}
