package co.tunan.tucache.core;

import co.tunan.tucache.core.aspect.TuCacheAspect;
import co.tunan.tucache.core.cache.TuCacheService;
import co.tunan.tucache.core.config.TuCacheProfiles;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.beans.factory.support.BeanDefinitionRegistryPostProcessor;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.lang.Nullable;

public class TuCacheBean implements BeanDefinitionRegistryPostProcessor, BeanFactoryAware, InitializingBean, DisposableBean {

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
    }

    /**
     * Implement the DisposableBean interface, cleanup static variables when the Context is closed.
     */
    @Override
    public void destroy() throws Exception {

        TuCacheAspect tuCacheAspect = beanFactory.getBean(TuCacheAspect.class);
        tuCacheAspect.getThreadPool().shutdown();

        log.info("tucache is destroy");
    }

    public void setTuCacheService(TuCacheService tuCacheService) {

        this.tuCacheService = tuCacheService;
    }

    public void setTuCacheProfiles(TuCacheProfiles tuCacheProfiles) {
        this.tuCacheProfiles = tuCacheProfiles;
    }

    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException {
        if (this.tuCacheService == null) {
            log.warn("TuCacheService at least one implementation, or closed tucache[tucache.enable=false]");
        }

        GenericBeanDefinition beanDefinition = new GenericBeanDefinition();
        beanDefinition.setBeanClass(TuCacheAspect.class);

        MutablePropertyValues propertyValues = new MutablePropertyValues();
        propertyValues.add("tuCacheService", tuCacheService);
        propertyValues.add("tuCacheProfiles", tuCacheProfiles);

        beanDefinition.setPropertyValues(propertyValues);

        registry.registerBeanDefinition("tuCacheAspect", beanDefinition);

    }

    @Override
    public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
    }
}
