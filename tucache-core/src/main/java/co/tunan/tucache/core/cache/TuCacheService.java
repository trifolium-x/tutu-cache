package co.tunan.tucache.core.cache;

/**
 * This interface stipulates some basic caching interfaces used by TuCache.
 * If users need to use their own caches,
 * implement this interface to replace the default redis cache
 *
 * @see
 *
 * @author : wangxudong
 * @date: 2020/08/28 10:48
 */
public interface TuCacheService {

    /**
     * add cache and set time
     * @param expire 单位秒 s
     */
    void set(String key, Object value, long expire);

    /**
     * add cache
     */
    void set(String key, Object value);

    /**
     * get the data in the buffer
     */
    <T> T get(String key, Class<T> clazz);

    /**
     * get the data in the buffer and set time
     */
    <T> T get(String key, Class<T> clazz, long expire);

    /**
     * delete cache
     */
    void delete(String key);

    /**
     * fuzzy delete
     */
    void deleteKeys(String key);
}
