package co.tunan.tucache.core.annotation;

import co.tunan.tucache.core.aspect.TuCacheAspect;

import java.lang.annotation.*;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 * 该注解用于缓存方法返回的数据，key支持spEl表达式
 * 可指定缓存过期时间和redis文档目录
 * <p>
 * 如果从缓存中拿不到或者出现超时，异常等情况则从方法中拿
 *
 * @author wangxudong
 * @date 2020/08/28
 * @see TuCacheAspect
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface TuCache {

    /**
     * Annotation on the method, cache the return value of the method
     * when the cache is re-stored, the cache time will be reset
     * If resetExpire is set to false, if the cache exists in the cache, the data will be updated directly without resetting the time.
     * <p>
     * 注解在方法上，将方法的返回值进行缓存
     * expire为缓存过期时间，当缓存重新存入时，则缓存时间会重置
     * 如果设置resetExpire为false的情况下，如果缓存中存在该缓存，则直接更新数据而不会重置时间。
     * <p>
     * Alias for {@link #key()}.
     */
    String value() default "";

    /**
     * Alias for {@link #value()}.
     */
    String key() default "";

    /**
     * 缓存超时时间
     * Cache expire, seconds
     * Alias for {@link #timeout()}.
     * @see #timeout()
     * @deprecated {@link #timeout()} 计划在1.0.5版本之后会完全弃用
     */
    @Deprecated
    long expire() default -1;

    /**
     * 缓存超时时间
     * Alias for {@link #expire()}.
     * Cache timeout, in seconds.
     * default value is permanent (-1)
     */
    long timeout() default -1;

    /**
     * 时间单位，默认为秒
     */
    TimeUnit timeUnit() default TimeUnit.SECONDS;

    /**
     * Write to the cache asynchronously
     * 使用异步方式写入结果到缓存
     */
    boolean async() default false;

    /**
     * Whether to reset the time every time the cache is hit
     * 是否每次命中缓存则重置时间
     */
    boolean resetExpire() default false;

    /**
     * 条件表达式
     * 使用spEL表达式来告诉TuCache是否需要生效
     * spEl表达式必须返回boolean
     */
    String condition() default "true";
}

