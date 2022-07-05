package co.tunan.tucache.core.aspect;

import co.tunan.tucache.core.annotation.TuCache;
import co.tunan.tucache.core.annotation.TuCacheClear;
import co.tunan.tucache.core.bean.TuConditionProcess;
import co.tunan.tucache.core.bean.TuKeyGenerate;
import co.tunan.tucache.core.bean.impl.DefaultTuKeyGenerate;
import co.tunan.tucache.core.cache.TuCacheService;
import co.tunan.tucache.core.config.TuCacheProfiles;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.StringUtils;

import java.lang.reflect.Method;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 缓存注解的切面实现
 *
 * @author wangxudong
 * @date 2020/08/28
 * @see TuCache,TuCacheClear
 */
@Aspect
public class TuCacheAspect implements DisposableBean, InitializingBean, BeanFactoryAware {

    private final static Logger log = LoggerFactory.getLogger(TuCacheAspect.class);

    private BeanFactory beanFactory;

    /**
     * 可缓存的线程池，用于提交异步任务
     */
    private ExecutorService threadPool;

    /**
     * 需要 tuCacheService 如果没有注入则发生异常
     */
    private TuCacheService tuCacheService;

    /**
     * tuCache 的 key 生成器，如果没有注入，则使用默认的生成器
     */
    private TuKeyGenerate tuKeyGenerate;

    /**
     * 设置默认值
     */
    private TuCacheProfiles tuCacheProfiles = new TuCacheProfiles();

    @Around("@annotation(co.tunan.tucache.core.annotation.TuCache)")
    public Object cache(ProceedingJoinPoint pjp) throws Throwable {
        log.debug("tu-cache caching");

        if (tuCacheService != null) {

            Object targetObj = pjp.getTarget();
            Signature signature = pjp.getSignature();
            MethodSignature methodSignature = (MethodSignature) signature;
            Method method = methodSignature.getMethod();
            Class<?> returnType = method.getReturnType();
            Object[] args = pjp.getArgs();
            TuCache tuCache = method.getAnnotation(TuCache.class);

            if (returnType.equals(void.class)
                    || !new TuConditionProcess(this.beanFactory).accept(tuCache.condition(), targetObj, method, args)) {

                return pjp.proceed();
            }

            String spElKey = StringUtils.isEmpty(tuCache.key()) ? tuCache.value() : tuCache.key();

            String cacheKey = tuKeyGenerate.generate(tuCacheProfiles, spElKey, targetObj, method, args);

            Object cacheResult;

            // 从缓存中获取数据，如果出错，则直接返回方法处理
            try {
                if (tuCache.resetExpire()) {
                    // Get data and reset the expiration time
                    cacheResult = tuCacheService.get(cacheKey, returnType, tuCache.expire(), tuCache.timeUnit());
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
                            tuCacheService.set(cacheKey, cacheResult, tuCache.expire(), tuCache.timeUnit());
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

            if (!new TuConditionProcess(this.beanFactory).accept(tuCacheClear.condition(), targetObj, method, args)) {

                return pjp.proceed();
            }

            String[] key = tuCacheClear.key().length == 0 ? tuCacheClear.value() : tuCacheClear.key();
            String[] keys = tuCacheClear.keys();

            try {
                if (key.length > 0) {
                    for (String item : key) {
                        String ckey = tuKeyGenerate.generate(tuCacheProfiles, item, targetObj, method, args);
                        if (tuCacheClear.async()) {
                            threadPool.submit(() -> tuCacheService.delete(ckey));
                        } else {
                            tuCacheService.delete(ckey);
                        }
                    }
                }
                if (keys.length > 0) {
                    for (String item : keys) {
                        String ckey = tuKeyGenerate.generate(tuCacheProfiles, item, targetObj, method, args);
                        if (tuCacheClear.async()) {
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
    }

    public void setTuCacheProfiles(TuCacheProfiles tuCacheProfiles) {
        this.tuCacheProfiles = tuCacheProfiles;
    }

    public void setTuKeyGenerate(TuKeyGenerate tuKeyGenerate) {
        this.tuKeyGenerate = tuKeyGenerate;
    }

    public void setThreadPool(ExecutorService threadPool) {
        this.threadPool = threadPool;
    }

    @Override
    public void afterPropertiesSet() {

        if (tuCacheService == null) {
            log.warn("TuCacheService at least one implementation, or closed tu-cache[tucache.enable=false]");
        }
        // 如果没有注入tuKeyGenerate 则使用默认的KeyGenerate
        if (this.tuKeyGenerate == null) {
            this.tuKeyGenerate = new DefaultTuKeyGenerate(beanFactory);
        }

        if (threadPool == null && tuCacheService != null) {
            threadPool = new ThreadPoolExecutor(tuCacheProfiles.getPool().getCorePoolSize(),
                    tuCacheProfiles.getPool().getMaximumPoolSize(),
                    tuCacheProfiles.getPool().getKeepAliveTime(), TimeUnit.MILLISECONDS,
                    new LinkedBlockingQueue<>(tuCacheProfiles.getPool().getMaxQueueSize()),
                    new NamedThreadFactory(),
                    new ThreadPoolExecutor.AbortPolicy());
        }
    }

    @Override
    public void destroy() {
        if (this.threadPool != null) {
            this.threadPool.shutdown();
        }

        log.info("tu-cache is destroy");
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {

        this.beanFactory = beanFactory;
    }


    public static class NamedThreadFactory implements ThreadFactory {
        private final AtomicInteger threadNumber = new AtomicInteger(1);

        @Override
        public Thread newThread(Runnable r) {
            final Thread t = new Thread(null, r, "tu-cache-pool-" + threadNumber.getAndIncrement());
            t.setDaemon(false);
            //优先级
            if (Thread.NORM_PRIORITY != t.getPriority()) {
                // 标准优先级
                t.setPriority(Thread.NORM_PRIORITY);
            }
            return t;
        }

    }

}
