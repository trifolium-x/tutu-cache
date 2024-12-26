package io.github.tri5m.tucache.core.cache;

import java.lang.reflect.Method;

/**
 * User-defined TuCacheService should <strong>not inherit</strong> this class
 * <p>
 * 用户不要继承该类，否则无法自动推导
 * </p>
 *
 * @title: AbstractTuCacheService
 * @author: trifolium.wang
 * @date: 2023/9/20
 * @modified :
 */
public abstract class AbstractTuCacheService implements TuCacheService {
    // User-defined TuCacheService should <strong>not inherit</strong> this class
    // 用户不要继承该类，否则无法自动推导用户缓存实现

    protected <T> T objectConvertBean(Object obj, Class<T> clazz) {

        if (obj == null) {
            return null;
        }

        if (clazz.isArray() || clazz.isPrimitive()) {
            return (T) obj;
        }

        if (obj instanceof Number) {
            if (clazz == Long.class) {
                return clazz.cast(((Number) obj).longValue());
            }
            if (clazz == Integer.class) {
                return clazz.cast(((Number) obj).intValue());
            }
            if (clazz == Double.class) {
                return clazz.cast(((Number) obj).doubleValue());
            }
            if (clazz == Float.class) {
                return clazz.cast(((Number) obj).floatValue());
            }
            if (clazz == Short.class) {
                return clazz.cast(((Number) obj).shortValue());
            }
            if (clazz == Byte.class) {
                return clazz.cast(((Number) obj).byteValue());
            }
        }

        try {
            if (clazz.isEnum()) {
                Method method = clazz.getMethod("valueOf", String.class);

                return clazz.cast(method.invoke("valueOf", obj.toString()));
            }
        } catch (Exception e) {

            throw new RuntimeException(e.getMessage(), e);
        }

        return clazz.cast(obj);
    }
}
