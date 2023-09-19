package co.tunan.tucache.autoconfigure.configure.cache;

import co.tunan.tucache.core.cache.TuCacheService;
import co.tunan.tucache.core.cache.impl.LocalCacheService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;

/**
 * @title: LocalCacheServiceConfigure 本地自定义缓存
 * @author: trifolium.wang
 * @date: 2023/9/19
 * @modified :
 */
@Slf4j
public class LocalCacheServiceConfigure {

    public LocalCacheServiceConfigure() {
        System.out.println("初始化了：LocalCacheServiceConfigure");
    }

    @Bean("localTuCacheService")
    public TuCacheService localTuCacheService() {

        // TODO
        LocalCacheService localCacheService = new LocalCacheService();

        return localCacheService;
    }
}
