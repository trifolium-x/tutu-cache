package co.tunan.tucache.autoconfigure.configure;

import co.tunan.tucache.autoconfigure.configure.cache.CaffeineCacheServiceConfigure;
import co.tunan.tucache.autoconfigure.configure.cache.LocalCacheServiceConfigure;
import co.tunan.tucache.autoconfigure.configure.cache.RedisCacheServiceConfigure;
import co.tunan.tucache.core.aspect.TuCacheAspect;
import co.tunan.tucache.core.bean.TuKeyGenerate;
import co.tunan.tucache.core.bean.impl.DefaultTuKeyGenerate;
import co.tunan.tucache.core.cache.TuCacheService;
import co.tunan.tucache.core.cache.impl.CaffeineCacheService;
import co.tunan.tucache.core.cache.impl.LocalCacheService;
import co.tunan.tucache.core.cache.impl.RedisCacheService;
import co.tunan.tucache.core.config.TuCacheProfiles;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.cache.CacheAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * tu-cache springboot auto configuration
 *
 * @author wangxudong
 * @date 2020/08/28
 */
@Slf4j
@Configuration(proxyBeanMethods = false)
@ConditionalOnClass({TuCacheService.class})
@AutoConfigureAfter({CacheAutoConfiguration.class})
@EnableConfigurationProperties(TuCacheProfilesConfigure.class)
//@Import({TuCacheServiceSelector.class})
@Import({CaffeineCacheServiceConfigure.class, RedisCacheServiceConfigure.class, LocalCacheServiceConfigure.class})
@ConditionalOnProperty(prefix = "tucache", name = "enabled", havingValue = "true", matchIfMissing = true)
public class TuCacheAutoConfigure {

    @Bean
    @ConditionalOnMissingBean
    public TuCacheAspect tuCacheAspect(TuCacheProfilesConfigure tuCacheConfigure,
                                       ObjectProvider<TuCacheService> tuCacheServices,
                                       ObjectProvider<TuKeyGenerate> tuKeyGenerates) {

        TuCacheService tuCacheService = selectTuCacheService(tuCacheServices,
                tuCacheConfigure.getProfiles().getCacheType());

        log.info("tu-cache service: {}", tuCacheService.getClass().getTypeName());

        TuCacheAspect tuCacheAspect = new TuCacheAspect();
        tuCacheAspect.setTuCacheProfiles(tuCacheConfigure.getProfiles());
        tuCacheAspect.setTuCacheService(tuCacheService);

        tuKeyGenerates.ifAvailable(tuCacheAspect::setTuKeyGenerate);

        return tuCacheAspect;
    }

    @Bean
    @ConditionalOnMissingBean(TuKeyGenerate.class)
    public TuKeyGenerate tuKeyGenerate(BeanFactory beanFactory) {

        return new DefaultTuKeyGenerate(beanFactory);
    }

    /**
     * 按照优先级或者用户配置选择合适的缓存服务
     */
    private TuCacheService selectTuCacheService(ObjectProvider<TuCacheService> tuCacheServices,
                                                TuCacheProfiles.CacheType cacheType) {

        TuCacheService tuCacheService;

        switch (cacheType) {
            case CAFFEINE:
                tuCacheService = tuCacheServices.stream().filter(cs -> cs instanceof CaffeineCacheService)
                        .findFirst().orElseThrow(() -> new BeanCreationException("CaffeineCacheService",
                                "CaffeineCacheService bean does not exist"));
                break;
            case LOCAL:
                tuCacheService = tuCacheServices.stream().filter(cs -> cs instanceof LocalCacheService)
                        .findFirst().orElseThrow(() -> new BeanCreationException("LocalCacheService",
                                "LocalCacheService bean does not exist"));

                break;
            case REDIS:
                tuCacheService = tuCacheServices.stream().filter(cs -> cs instanceof RedisCacheService)
                        .findFirst().orElseThrow(() -> new BeanCreationException("RedisCacheService",
                                "RedisCacheService bean does not exist"));

                break;
            default:
                // 根据 CUSTOM > REDIS > CAFFEINE > LOCAL的优先级选用，
                // 如果有@Primary注解的或者是唯一的TuCacheService则直接选择
                tuCacheService = tuCacheServices.getIfUnique();
                if (tuCacheService != null) {
                    break;
                }

                // 有用户自定义的或者多个缓存组件
                tuCacheService = tuCacheServices.stream().filter(cs ->
                                !(cs instanceof RedisCacheService)
                                        && !(cs instanceof CaffeineCacheService)
                                        && !(cs instanceof LocalCacheService)).findFirst()
                        .orElse(null);
                // 如果是用户自定义，则自定义优先
                if (tuCacheService != null) {
                    break;
                }

                tuCacheService = tuCacheServices.stream().filter(cs -> (cs instanceof RedisCacheService)).findFirst()
                        .orElse(null);
                if (tuCacheService != null) {
                    break;
                }

                tuCacheService = tuCacheServices.stream().filter(cs -> (cs instanceof CaffeineCacheService)).findFirst()
                        .orElse(null);
                if (tuCacheService != null) {
                    break;
                }

                // 下面代码实际上不可触达，不过为了可读性就写上去了
                tuCacheService = tuCacheServices.stream().filter(cs -> (cs instanceof LocalCacheService)).findFirst()
                        .orElse(null);
                if (tuCacheService != null) {
                    break;
                }
        }

        return tuCacheService;
    }
}
