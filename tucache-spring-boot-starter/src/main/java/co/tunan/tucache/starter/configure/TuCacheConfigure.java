package co.tunan.tucache.starter.configure;

import co.tunan.tucache.core.config.TuCacheProfiles;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("tucache")
public class TuCacheConfigure {

    private boolean enable = true;

    private TuCacheProfiles profiles;

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    public TuCacheProfiles getProfiles() {
        return profiles;
    }

    public void setProfiles(TuCacheProfiles profiles) {
        this.profiles = profiles;
    }
}
