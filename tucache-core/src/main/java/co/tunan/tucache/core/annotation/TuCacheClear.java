package co.tunan.tucache.core.annotation;

import co.tunan.tucache.core.aspect.TuCacheAspect;
import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.*;

/**
 * @author : wangxudong
 * @title:
 * @date: 2020/04/09 10:56
 * @version: 1.0
 * @modified :
 * @see TuCacheAspect
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface TuCacheClear {

    /**
     * delete the corresponding key cache, keys means delete all caches beginning with keys (fuzzy deletion)
     * {@example If there is a cache with a key of example:123:keys, when the keys is example:, all caches starting with example: will be deleted}
     * key和keys都可以指定多个
     * <p>
     * condition功能未实现。
     */

    @AliasFor("key")
    String[] value() default {};

    String[] key() default {};

    String[] keys() default {};

    /**
     * Whether to perform cache clearing asynchronously. If the real-time performance of cache deletion is not strictly required, you can use asynchronous cache clearing to improve performance
     * 是否异步执行缓存清理，如果不严格要求缓存删除的实时性，提高性能可以使用异步方式进行清除缓存
     */
    boolean async() default false;

    String condition() default "true";
}
