package co.tunan.tucache.autoconfigure.configure.cache;

import co.tunan.tucache.autoconfigure.configure.TuCacheCondition;
import co.tunan.tucache.core.cache.TuCacheService;
import co.tunan.tucache.core.cache.impl.LocalCacheService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Conditional;

/**
 * @title: LocalCacheServiceConfigure 本地自定义缓存
 * @author: trifolium.wang
 * @date: 2023/9/19
 * @modified :
 */
@Conditional(TuCacheCondition.class)
public class LocalCacheServiceConfigure {

    public LocalCacheServiceConfigure(){
        System.out.println("注入了...LocalCacheServiceConfigure");
    }

    @Bean("localTuCacheService")
    public TuCacheService localTuCacheService() {
        System.out.println("local");
        return new LocalCacheService();
    }
}
