package co.tunan.tucache.starter.configure;

import co.tunan.tucache.core.aspect.TuCacheAspect;
import co.tunan.tucache.core.cache.TuCacheService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnMissingBean(TuCacheAspect.class)
@EnableConfigurationProperties(TuCacheConfigure.class)
public class TuCacheAutoConfigure {

    @Autowired(required = false)
    private TuCacheService tuCacheService;

    @Autowired
    private TuCacheConfigure tuCacheConfigure;

    @Bean
    @ConditionalOnMissingBean
    @ConditionalOnExpression("#tuCacheConfigure.enable")
    public TuCacheAspect starterService() {

        return null;
    }

}
