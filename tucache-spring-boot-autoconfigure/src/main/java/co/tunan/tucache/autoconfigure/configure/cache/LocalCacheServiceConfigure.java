package co.tunan.tucache.autoconfigure.configure.cache;

import co.tunan.tucache.autoconfigure.configure.TuCacheCondition;
import co.tunan.tucache.core.cache.TuCacheService;
import co.tunan.tucache.core.cache.impl.LocalCacheService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;

/**
 * @title: LocalCacheServiceConfigure 本地自定义缓存
 * @author: trifolium.wang
 * @date: 2023/9/19
 * @modified :
 */
@Slf4j
@Conditional(TuCacheCondition.class)
public class LocalCacheServiceConfigure {

    @Bean("localTuCacheService")
    public TuCacheService localTuCacheService() {

        log.debug("Injected with LocalTuCacheService");

        return new LocalCacheService();
    }
}
