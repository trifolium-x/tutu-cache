package co.tunan.tucache.core.cache.impl;

import co.tunan.tucache.core.cache.TuCacheService;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.CollectionUtils;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

/**
 * The default TuCacheService implementation class
 * Created by v_wangxudong on 2019/3/14.
 */
public class RedisCacheService implements TuCacheService {

//    private RedisTemplate<String, Object> redisTemplate;

    private Supplier<Object> redisTemplateSupplier;

    private List<RedisTemplate<Object, Object>> redisTemplates;

    private static final long NOT_EXPIRE = -1;

    @Override
    public void set(String key, Object value, long expire) {
        getRedisTemplate().opsForValue().set(key, value);
        if (expire != NOT_EXPIRE) {
            getRedisTemplate().expire(key, expire, TimeUnit.SECONDS);
        }
    }

    @Override
    public void set(String key, Object value) {
        set(key, value, NOT_EXPIRE);
    }

    @Override
    public void delete(String key) {
        getRedisTemplate().delete(key);
    }

    @Override
    public void deleteKeys(String key) {
        Set<Object> keys = getRedisTemplate().keys(key + "*");
        getRedisTemplate().delete(keys);
    }

    @Override
    public <T> T get(String key, Class<T> clazz) {
        return get(key, clazz, NOT_EXPIRE);
    }

    @Override
    public <T> T get(String key, Class<T> clazz, long expire) {
        Object value = getRedisTemplate().opsForValue().get(key);

        if (expire != NOT_EXPIRE) {
            getRedisTemplate().expire(key, expire, TimeUnit.SECONDS);
        }

        if (value == null)
            return null;

        if (clazz.isPrimitive())
            return (T) value;

        try {
            if (value instanceof Number) {
                Method method = clazz.getMethod("valueOf", String.class);

                return clazz.cast(method.invoke("valueOf", value.toString()));
            }
        } catch (Exception e) {

            throw new RuntimeException(e.getMessage(), e);
        }

        return clazz.cast(value);

    }

    public void setRedisTemplateSupplier(Supplier<Object> supplier) {
        this.redisTemplateSupplier = supplier;
    }

    private RedisTemplate<Object, Object> getRedisTemplate() {

        if (CollectionUtils.isEmpty(redisTemplates)) {
            RedisTemplate<Object, Object> redisTemplate = (RedisTemplate<Object, Object>)redisTemplateSupplier.get();

            if (redisTemplate == null) {
                throw new NullPointerException("redis template is null.");
            }
            this.redisTemplates = new ArrayList<>(1);
            redisTemplates.add(redisTemplate);
        }

        return redisTemplates.get(0);
    }
}

