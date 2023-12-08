package co.tunan.tucache.core.cache.impl;

import co.tunan.tucache.core.cache.AbstractTuCacheService;
import co.tunan.tucache.core.localcache.TuTreeCache;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

/**
 * @title: LocalCacheService
 * @author: trifolium.wang
 * @date: 2023/9/19
 * @modified :
 */
@Slf4j
public class LocalCacheService extends AbstractTuCacheService {

    private final TuTreeCache tuTreeCache;

    public LocalCacheService() {
        tuTreeCache = new TuTreeCache();
    }

    @Override
    public void set(String key, Object value, long timeout, TimeUnit timeUnit) {
        tuTreeCache.putNode(key, value, timeout < 0 ? null : timeUnit.toMillis(timeout));
    }

    @Override
    public void set(String key, Object value) {
        set(key, value, TuTreeCache.NOT_EXPIRE, TimeUnit.SECONDS);
    }

    @Override
    public <T> T get(String key, Class<T> clazz) {
        TuTreeCache.CacheNode cacheNode = tuTreeCache.searchNode(key);
        if (cacheNode != null) {
            return objectConvertBean(cacheNode.getObj(), clazz);
        }

        return null;
    }

    @Override
    public <T> T get(String key, Class<T> clazz, long timeout, TimeUnit timeUnit) {
        TuTreeCache.CacheNode cacheNode = tuTreeCache.searchNode(key);
        if (cacheNode != null) {
            cacheNode.setExpire(timeout < 0 ? TuTreeCache.NOT_EXPIRE
                    : (timeUnit.toMillis(timeout) + System.currentTimeMillis()));
            return objectConvertBean(cacheNode.getObj(), clazz);
        }

        return null;
    }

    @Override
    public void delete(String key) {
        tuTreeCache.remove(key);
    }

    @Override
    public void deleteKeys(String key) {
        String k = key;
        while (k.endsWith("*")) {
            k = k.substring(0, k.length() - 1);
        }
        tuTreeCache.removeKeys(k);
    }

}
