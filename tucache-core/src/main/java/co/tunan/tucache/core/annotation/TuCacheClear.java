package co.tunan.tucache.core.annotation;

import co.tunan.tucache.core.aspect.TuCacheAspect;

import java.lang.annotation.*;

/**
 * This annotation on a method that specifies that the key or keys will clean up the response cache
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
     * If there is a cache with a key of example:123:keys, when the keys is example.
     * all caches starting with example: will be deleted
     * You can specify multiple keys and keys.
     * <p></p>
     * Note that keys can only be split by ":" as a minimum finesse, e.g.
     * <pre>key = "aaa:bbb:ccc"</pre> using <pre>keys = aaa:bbb:c</pre> fuzzy deletion, such usage is wrong at least in LocalCache.
     * <p></p>
     * Try not to use keys mirroring cache deletion, which leads to very slow deletion,
     * or async = true to enable asynchronous deletion, which does not avoid memory pressure if there are too many keys.
     * Note that the minimum granularity for cache deletion using fuzzy deletion is the ":" split level, if it is not a full level then it will be a problem in LocalCache,
     * or lead to problems with local and distributed caches being out of sync.
     * Alias for {@link #key()}
     * <p></p>
     * 尽量不使用keys镜像缓存删除，这会导致删除的速度非常缓慢，或者async = true开启异步删除
     * ，但无法避免如果key过多导致的内存压力。
     * 注意，缓存使用模糊删除的最小粒度是":"分割的层级，如果不是一个完整的层则在LocalCache中会存在问题，或者导致本地和分布式缓存不同步问题。
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
