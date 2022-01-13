package co.tunan.tucache.autoconfigure.configure;

import co.tunan.tucache.core.aspect.TuCacheAspect;
import co.tunan.tucache.core.bean.TuKeyGenerate;
import co.tunan.tucache.core.bean.impl.DefaultTuKeyGenerate;
import co.tunan.tucache.core.cache.TuCacheService;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.FactoryBean;
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
@Configuration
@ConditionalOnClass({TuCacheService.class})
@AutoConfigureAfter({CacheAutoConfiguration.class})
@EnableConfigurationProperties(TuCacheConfigure.class)
@Import({RedisTuCacheConfigure.class})
@ConditionalOnProperty(prefix = "tucache", name = "enabled", havingValue = "true", matchIfMissing = true)
public class TuCacheAutoConfigure {

    @Bean
    public TuCacheAspect tuCacheAspect(TuCacheConfigure tuCacheConfigure,
                                       ObjectProvider<TuCacheService> tuCacheServices,
                                       ObjectProvider<TuKeyGenerate> tuKeyGenerates) {

        TuCacheService tuCacheService = tuCacheServices.getIfAvailable();

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

}
