package co.tunan.tucache.autoconfigure.configure;

import co.tunan.tucache.core.config.TuCacheProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

@ConfigurationProperties("tucache")
public class TuCacheConfigure {

    private boolean enabled = true;

    @NestedConfigurationProperty
    private TuCacheProperties properties = new TuCacheProperties();

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public TuCacheProperties getProperties() {
        return properties;
    }

    public void setProperties(TuCacheProperties properties) {
        this.properties = properties;
    }

    @Override
    public String toString() {

        return "enable:" + enabled + "," + this.properties;
    }

}
