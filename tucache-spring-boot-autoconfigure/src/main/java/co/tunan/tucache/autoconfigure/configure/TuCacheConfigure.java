package co.tunan.tucache.autoconfigure.configure;

import co.tunan.tucache.core.config.TuCacheProfiles;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

@ConfigurationProperties("tucache")
public class TuCacheConfigure {

    private boolean enable = true;

    @NestedConfigurationProperty
    private TuCacheProfiles profiles = new TuCacheProfiles();

    public TuCacheProfiles getProfiles() {
        return profiles;
    }

    public void setProfiles(TuCacheProfiles profiles) {
        this.profiles = profiles;
    }

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }


    @PostConstruct
    public void init() {

    }

    @Override
    public String toString() {

        return "enable:" + enable + "," + this.profiles;
    }
}
