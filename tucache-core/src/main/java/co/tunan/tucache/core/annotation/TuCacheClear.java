package co.tunan.tucache.core.annotation;

import co.tunan.tucache.core.aspect.TuCacheAspect;

import java.lang.annotation.*;

/**
 * 该注解在方法上，指定key或者keys将会清理响应的缓存
 *
 * @author wangxudong
 * @date 2020/08/28
 * @see TuCacheAspect
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface TuCacheClear {

    /**
     * delete the corresponding cache, keys means delete all caches beginning with keys (fuzzy deletion)
     * If there is a cache with a key of example:123:keys, when the keys is example:,
     * all caches starting with example: will be deleted
     * key和keys都可以指定多个
     * <p>
     * Alias for {@link #key()}.
     */
    String[] value() default {};

    /**
     * Alias for {@link #value()}.
     */
    String[] key() default {};

    /**
     * @see #value
     */
    String[] keys() default {};

    /**
     * Whether to perform cache clearing asynchronously. If the real-time performance of cache deletion is not strictly
     * required, you can use asynchronous cache clearing to improve performance
     * 是否异步执行缓存清理，如果不严格要求缓存删除的实时性，提高性能可以使用异步方式进行清除缓存
     */
    boolean async() default false;

    String condition() default "true";

}
