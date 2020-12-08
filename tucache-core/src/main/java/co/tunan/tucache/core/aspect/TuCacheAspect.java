package co.tunan.tucache.core.aspect;

import co.tunan.tucache.core.annotation.TuCache;
import co.tunan.tucache.core.annotation.TuCacheClear;
import co.tunan.tucache.core.cache.TuCacheService;
import co.tunan.tucache.core.config.TuCacheProfiles;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.expression.BeanExpressionContextAccessor;
import org.springframework.context.expression.BeanFactoryAccessor;
import org.springframework.context.expression.MapAccessor;
import org.springframework.context.expression.MethodBasedEvaluationContext;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.ParserContext;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.util.StringUtils;

import java.lang.reflect.Method;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by wangxudong on 2020/04/09.
 *
 * @version: 1.0
 * @modified :
 */
@Aspect
public class TuCacheAspect {

    private final static Logger log = LoggerFactory.getLogger(TuCacheAspect.class);


    /**
     * 可缓存的线程池，用于提交异步任务
     */
    public static final ExecutorService threadPool = Executors.newCachedThreadPool();

    private TuCacheService tuCacheService;

    private TuCacheProfiles tuCacheProfiles;

    @Around("@annotation(co.tunan.tucache.core.annotation.TuCache)")
    public Object cache(ProceedingJoinPoint pjp) throws Throwable {
        log.debug("tu-cache caching");

        Object cacheResult;
        Signature signature = pjp.getSignature();
        MethodSignature methodSignature = (MethodSignature) signature;
        Method targetMethod = methodSignature.getMethod();
        TuCache tuCache = targetMethod.getAnnotation(TuCache.class);
        if (tuCache == null) {

            return pjp.proceed();
        }
        Method method = getMethod(pjp);
        Object[] args = pjp.getArgs();
        String key = StringUtils.isEmpty(tuCache.key()) ? tuCache.value() : tuCache.key();

        Object targetObj = pjp.getTarget();
        String cacheKey = parseKey(key, targetObj, method, args);
        Class<?> returnType = method.getReturnType();
        if (returnType.equals(void.class)) {

            return pjp.proceed();
        }
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

                return cacheResult;
            }
        }

        return cacheResult;
    }

    @Around("@annotation(co.tunan.tucache.core.annotation.TuCacheClear)")
    public Object clear(ProceedingJoinPoint pjp) throws Throwable {
        log.debug("tu-cache clear.");
        Signature signature = pjp.getSignature();
        MethodSignature methodSignature = (MethodSignature) signature;
        Method targetMethod = methodSignature.getMethod();
        TuCacheClear tuCacheClear = targetMethod.getAnnotation(TuCacheClear.class);
        if (tuCacheClear != null) {
            Method method = getMethod(pjp);
            Object[] args = pjp.getArgs();
            String[] key = tuCacheClear.key().length == 0 ? tuCacheClear.value() : tuCacheClear.key();
            String[] keys = tuCacheClear.keys();
            Object targetObj = pjp.getTarget();
            try {
                if (key.length > 0) {
                    for (String item : key) {
                        String ckey = parseKey(item, targetObj, method, args);
                        if (tuCacheClear.sync()) {
                            threadPool.submit(() -> tuCacheService.delete(ckey));
                        } else {
                            tuCacheService.delete(ckey);
                        }
                    }
                }
                if (keys.length > 0) {
                    for (String item : keys) {
                        String ckey = parseKey(item, targetObj, method, args);
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
    }

    private Method getMethod(ProceedingJoinPoint pjp) throws NoSuchMethodException {
        //获取参数的类型
        Class[] argTypes = ((MethodSignature) pjp.getSignature()).getParameterTypes();
        Method method = pjp.getTarget().getClass().getMethod(pjp.getSignature().getName(), argTypes);

        return method;

    }

    private String parseKey(String spEl, Object targetObj, Method method, Object[] args) {
        // SpEL表达式为空默认返回方法名
        if (StringUtils.isEmpty(spEl)) {
            // 生成默认的key
            return defaultKey(method, args);
        }
        ExpressionParser parser = new SpelExpressionParser();
        ParserContext parserContext = new ParserContext() {
            @Override
            public boolean isTemplate() {
                return true;
            }

            @Override
            public String getExpressionPrefix() {
                return "#{";
            }

            @Override
            public String getExpressionSuffix() {
                return "}";
            }
        };
        StandardEvaluationContext context = new MethodBasedEvaluationContext(targetObj, method, args,
                new DefaultParameterNameDiscoverer());

        String keyPrefix = "";
        if (tuCacheProfiles != null && !StringUtils.isEmpty(tuCacheProfiles.getCachePrefix())) {
            keyPrefix = tuCacheProfiles.getCachePrefix();
        }

        return keyPrefix + parser.parseExpression(spEl, parserContext).getValue(context, String.class);
    }

    private String defaultKey(Method method, Object[] args) {
        String defaultKey = method.getDeclaringClass().getPackage().getName() + method.getDeclaringClass().getName() + ":" + method.getName();
        StringBuilder builder = new StringBuilder(defaultKey);
        for (Object a : args) {
            builder.append(a.hashCode()).append("_");
        }

        return builder.toString();
    }

    public void setTuCacheProfiles(TuCacheProfiles tuCacheProfiles) {
        this.tuCacheProfiles = tuCacheProfiles;
    }
}
