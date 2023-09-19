//package co.tunan.tucache.autoconfigure.configure;
//
//import org.springframework.context.annotation.ImportSelector;
//import org.springframework.core.type.AnnotationMetadata;
//
///**
// * @title: TuCacheServiceSelector
// * @author: trifolium.wang
// * @date: 2023/9/19
// * @modified :
// */
//public class TuCacheServiceSelector implements ImportSelector {
//
//    private final TuCacheProfilesConfigure tuCacheConfigure;
//
//    public TuCacheServiceSelector(TuCacheProfilesConfigure tuCacheConfigure) {
//        this.tuCacheConfigure = tuCacheConfigure;
//    }
//
//    @Override
//    public String[] selectImports(AnnotationMetadata importingClassMetadata) {
//
//        String[] clazzPath;
//
//        switch (tuCacheConfigure.getProfiles().getCacheType()) {
//            case CAFFEINE:
//            case LOCAL:
//                clazzPath = new String[]{"co.tunan.tucache.autoconfigure.configure.cache.LocalCacheServiceConfigure"};
//                break;
//            case REDIS:
//                clazzPath = new String[]{"co.tunan.tucache.autoconfigure.configure.cache.RedisCacheServiceConfigure"};
//
//                break;
//            default:
//                // TODO 判断这两个类的条件，如果条件通过则添加，否则全不添加
//                clazzPath = new String[]{"co.tunan.tucache.autoconfigure.configure.cache.LocalCacheServiceConfigure",
//                        "co.tunan.tucache.autoconfigure.configure.cache.RedisCacheServiceConfigure"};
//        }
//
//        return clazzPath;
//    }
//}
