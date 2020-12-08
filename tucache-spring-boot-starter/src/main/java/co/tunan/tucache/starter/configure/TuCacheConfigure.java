package co.tunan.tucache.starter.configure;

import co.tunan.tucache.core.config.TuCacheProfiles;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("tucache")
public class TuCacheConfigure {

    private boolean enable = true;

    private TuCacheProfiles profiles;
}
