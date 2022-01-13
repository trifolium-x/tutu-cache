package co.tunan.tucache.core.bean;

import co.tunan.tucache.core.config.TuCacheProfiles;

import java.lang.reflect.Method;

/**
 * key generate
 *
 * @author wangxudong
 * @date 2020/08/28
 */
public interface TuKeyGenerate {

    /**
     * 根据传入的数据生成影响的key,用于缓存的key
     * @param profiles tuCache 配置文件
     * @param originKey 用户在注解中设置的key
     * @param rootObject 当前注解的所在对象
     * @param method  当前注解的所在方法
     * @param arguments 当前方法参数
     * @return 生成后的key
     */
    String generate(TuCacheProfiles profiles, String originKey, Object rootObject, Method method, Object[] arguments);
}
