package co.tunan.tucache.autoconfigure.configure;

import co.tunan.tucache.core.config.TuCacheProfiles;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

@ConfigurationProperties("tucache")
public class TuCacheConfigure {

    private boolean enabled = true;

    @NestedConfigurationProperty
    private TuCacheProfiles profiles = new TuCacheProfiles();

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public TuCacheProfiles getProfiles() {
        return profiles;
    }

    public void setProfiles(TuCacheProfiles profiles) {
        this.profiles = profiles;
    }

    @Override
    public String toString() {

        return "enable:" + enabled + "," + this.profiles;
    }
}
