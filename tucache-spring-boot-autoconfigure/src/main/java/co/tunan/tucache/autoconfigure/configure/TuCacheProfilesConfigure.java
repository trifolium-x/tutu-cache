package co.tunan.tucache.autoconfigure.configure;

import co.tunan.tucache.core.config.TuCacheProfiles;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

/**
 * tu-cache configuration
 *
 * @author wangxudong
 * @date 2020/08/28
 */
@ConfigurationProperties("tucache")
public class TuCacheProfilesConfigure {

    private boolean enabled = true;

    @Getter
    @Setter
    @NestedConfigurationProperty
    private TuCacheProfiles profiles = new TuCacheProfiles();

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    public String toString() {

        return "enable:" + enabled + "," + this.profiles;
    }

}
