package co.tunan.tucache.core.config;

public class TuCacheProfiles {

    /**
     * 缓存的统一key前缀，默认为 ""
     */
    private String cachePrefix = "";

    public String getCachePrefix() {
        return cachePrefix;
    }

    public void setCachePrefix(String cachePrefix) {
        this.cachePrefix = cachePrefix;
    }

    @Override
    public String toString() {

        return "cachePrefix:" + cachePrefix;
    }
}
