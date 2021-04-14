package co.tunan.tucache.core.aspect;

import co.tunan.tucache.core.annotation.TuCache;
import co.tunan.tucache.core.annotation.TuCacheClear;
import co.tunan.tucache.core.cache.TuCacheService;
import co.tunan.tucache.core.config.TuCacheProperties;
import co.tunan.tucache.core.util.SystemInfo;
import co.tunan.tucache.core.util.TuCacheUtil;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.util.StringUtils;

import java.lang.reflect.Method;
import java.util.concurrent.*;

/**
 * Created by wangxudong on 2020/04/09.
 *
 * @version: 1.0
 * @modified :
 */
@Aspect
public class TuCacheAspect implements DisposableBean {

    private final static Logger log = LoggerFactory.getLogger(TuCacheAspect.class);

    /**
     * 可缓存的线程池，用于提交异步任务
     */
    private ExecutorService threadPool = null;

    private TuCacheService tuCacheService;

    private TuCacheProperties tuCacheProperties = new TuCacheProperties();

    public TuCacheAspect() {

        initThreadPool();
    }

    @Around("@annotation(co.tunan.tucache.core.annotation.TuCache)")
    public Object cache(ProceedingJoinPoint pjp) throws Throwable {
        log.debug("tu-cache caching");

        if (tuCacheService != null) {

            Object targetObj = pjp.getTarget();
            Signature signature = pjp.getSignature();
            MethodSignature methodSignature = (MethodSignature) signature;
            Method method = methodSignature.getMethod();
            Class<?> returnType = method.getReturnType();
            if (returnType.equals(void.class)) {

                return pjp.proceed();
            }
            TuCache tuCache = method.getAnnotation(TuCache.class);

            Object[] args = pjp.getArgs();
            String spElKey = StringUtils.isEmpty(tuCache.key()) ? tuCache.value() : tuCache.key();

            String cacheKey = TuCacheUtil.parseKey(spElKey, targetObj, method, tuCacheProperties.getCachePrefix(), args);

            Object cacheResult;

            // 从缓存中获取数据，如果出错，则直接返回方法处理
            try {
                if (tuCache.resetExpire()) {
                    // Get data and reset the expiration time
                    cacheResult = tuCacheService.get(cacheKey, returnType, tuCache.expire());
                } else {
                    cacheResult = tuCacheService.get(cacheKey, returnType);
                }

            } catch (Exception e) {

                log.warn("cache miss,key:" + cacheKey);
                log.error(e.getMessage(), e);

                return pjp.proceed();
            }
            // 如果缓存中没有数据就放入，否则直接返回缓存的数据
            // 如果缓存中返回的是null，就认为没有缓存，直接运行方法获取最新数据
            if (cacheResult == null) {
                cacheResult = pjp.proceed();
                try {
                    if (cacheResult != null) {
                        if (tuCache.expire() == -1) {
                            tuCacheService.set(cacheKey, cacheResult);
                        } else {
                            tuCacheService.set(cacheKey, cacheResult, tuCache.expire());
                        }
                    }
                } catch (Exception e) {
                    log.warn("cache miss,key:" + cacheKey);
                    log.error(e.getMessage(), e);
                }
            }

            return cacheResult;
        }

        return pjp.proceed();
    }

    @Around("@annotation(co.tunan.tucache.core.annotation.TuCacheClear)")
    public Object clear(ProceedingJoinPoint pjp) throws Throwable {
        log.debug("tu-cache clear.");
        if (tuCacheService != null) {
            Object targetObj = pjp.getTarget();

            Signature signature = pjp.getSignature();
            MethodSignature methodSignature = (MethodSignature) signature;
            Method method = methodSignature.getMethod();
            TuCacheClear tuCacheClear = method.getAnnotation(TuCacheClear.class);
            Object[] args = pjp.getArgs();

            String[] key = tuCacheClear.key().length == 0 ? tuCacheClear.value() : tuCacheClear.key();
            String[] keys = tuCacheClear.keys();

            try {
                if (key.length > 0) {
                    for (String item : key) {
                        String ckey = TuCacheUtil.parseKey(item, targetObj, method,
                                tuCacheProperties.getCachePrefix(), args);
                        if (tuCacheClear.sync()) {
                            threadPool.submit(() -> tuCacheService.delete(ckey));
                        } else {
                            tuCacheService.delete(ckey);
                        }
                    }
                }
                if (keys.length > 0) {
                    for (String item : keys) {
                        String ckey = TuCacheUtil.parseKey(item, targetObj,
                                method, tuCacheProperties.getCachePrefix(), args);
                        if (tuCacheClear.sync()) {
                            threadPool.submit(() -> tuCacheService.deleteKeys(ckey));
                        } else {
                            tuCacheService.deleteKeys(ckey);
                        }
                    }
                }
            } catch (Exception e) {
                log.warn("failed to clean cache.");
                log.error(e.getMessage(), e);
            }
        }

        return pjp.proceed();
    }

    public void setTuCacheService(TuCacheService tuCacheService) {

        this.tuCacheService = tuCacheService;

        if (tuCacheService == null) {
            log.warn("TuCacheService at least one implementation, or closed tucache[tucache.enable=false]");
        }
    }


    public void setTuCacheProfiles(TuCacheProperties tuCacheProperties) {
        this.tuCacheProperties = tuCacheProperties;
    }

    /**
     * 默认线程池配置，核心线程为CPU核心数，最大线程为CPU核心*4
     * keepAliveTime 10秒，线程空闲时间超过10秒则关闭，保留核心线程
     * 队列长度为 Integer.MAX_VALUE
     */
    private void initThreadPool() {
        threadPool = new ThreadPoolExecutor(SystemInfo.MACHINE_CORE_NUM, SystemInfo.MACHINE_CORE_NUM * 4,
                10L, TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(Integer.MAX_VALUE),
                Executors.defaultThreadFactory(),
                new ThreadPoolExecutor.AbortPolicy());
    }

    public ExecutorService getThreadPool() {
        return threadPool;
    }

    /**
     * Implement the DisposableBean interface, cleanup static variables when the Context is closed.
     */
    @Override
    public void destroy() throws Exception {

        this.getThreadPool().shutdown();

        log.info("tucache is destroy");
    }
}
