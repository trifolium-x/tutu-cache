package co.tunan.tucache.core.bean;

import co.tunan.tucache.core.config.TuCacheProfiles;

import java.lang.reflect.Method;

public interface TuKeyGenerate {

    String generate(TuCacheProfiles profiles, String originKey, Object rootObject, Method method, Object[] arguments);
}
