package io.github.tri5m.tucache.autoconfigure.configure;

import io.github.tri5m.tucache.core.config.TuCacheProfiles;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

/**
 * tu-cache configuration
 *
 * @author wangxudong
 * @date 2020/08/28
 */
@Getter
@Setter
@ToString(callSuper = true)
@ConfigurationProperties("tucache")
public class TuCacheProfilesConfigure {


    private boolean enabled = true;

    /**
     * Specify the cache type, otherwise the cache component will be automatically inferred
     * AUTO cache-type Priority => custom > redis > redisson > local
     */
    private CacheType cacheType = CacheType.AUTO;

    @NestedConfigurationProperty
    private TuCacheProfiles profiles = new TuCacheProfiles();

    public enum CacheType {
        AUTO,
        REDIS,
        REDISSON,
        LOCAL;
    }
}
