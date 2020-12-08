package co.tunan.tucache.core;

import co.tunan.tucache.core.aspect.TuCacheAspect;
import co.tunan.tucache.core.cache.TuCacheService;
import co.tunan.tucache.core.cache.impl.RedisCacheService;
import co.tunan.tucache.core.config.TuCacheProfiles;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.lang.Nullable;

public class TuCacheBean implements BeanFactoryPostProcessor, BeanFactoryAware, InitializingBean, DisposableBean {

    private final static Logger log = LoggerFactory.getLogger(TuCacheBean.class);

    @Nullable
    private TuCacheService tuCacheService;

    private BeanFactory beanFactory;

    private TuCacheProfiles tuCacheProfiles;

    public TuCacheBean(TuCacheService tuCacheService) {

        this.tuCacheService = tuCacheService;
    }

    public TuCacheBean(){

    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {

        this.beanFactory = beanFactory;
    }

    @Override
    public void afterPropertiesSet() throws Exception {

        if (beanFactory == null) {
            throw new IllegalStateException("Cannot get default tuCacheService.");
        }

        if (tuCacheService == null) {
            RedisCacheService redisCacheService = new RedisCacheService();
            RedisTemplate redisTemplate = beanFactory.getBean(RedisTemplate.class);
            if (redisTemplate == null) {
                throw new IllegalStateException("set default redisCacheService failed，redisTemplate is not bean.");
            }
            redisCacheService.setRedisTemplate(redisTemplate);
            tuCacheService = redisCacheService;
        }
    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {

        if (this.tuCacheService == null) {
            throw new IllegalStateException("not find bean tuCacheService.");
        }
        TuCacheAspect tuCacheAspect = new TuCacheAspect();
        tuCacheAspect.setTuCacheService(tuCacheService);
        tuCacheAspect.setTuCacheProfiles(tuCacheProfiles);
        beanFactory.registerSingleton("tuCacheAspect", tuCacheAspect);
    }

    /**
     * Implement the DisposableBean interface, cleanup static variables when the Context is closed.
     */
    @Override
    public void destroy() throws Exception {

        TuCacheAspect tuCacheAspect = beanFactory.getBean(TuCacheAspect.class);
        if (tuCacheAspect != null) {
            tuCacheAspect.threadPool.shutdown();
        }
        log.info("TuCache is destroy");
    }

    public void setTuCacheService(TuCacheService tuCacheService){

        this.tuCacheService = tuCacheService;
    }

    public void setTuCacheProfiles(TuCacheProfiles tuCacheProfiles){
        this.tuCacheProfiles = tuCacheProfiles;
    }
}
