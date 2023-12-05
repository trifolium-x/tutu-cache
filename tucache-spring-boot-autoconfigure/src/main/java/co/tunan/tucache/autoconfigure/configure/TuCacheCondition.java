package co.tunan.tucache.autoconfigure.configure;

import co.tunan.tucache.autoconfigure.configure.cache.LocalCacheServiceConfigure;
import co.tunan.tucache.autoconfigure.configure.cache.RedisCacheServiceConfigure;
import org.springframework.boot.autoconfigure.condition.ConditionMessage;
import org.springframework.boot.autoconfigure.condition.ConditionOutcome;
import org.springframework.boot.autoconfigure.condition.SpringBootCondition;
import org.springframework.boot.context.properties.bind.BindResult;
import org.springframework.boot.context.properties.bind.Binder;
import org.springframework.context.annotation.ConditionContext;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.Environment;
import org.springframework.core.type.AnnotatedTypeMetadata;
import org.springframework.core.type.ClassMetadata;

/**
 * This class is mainly used to reduce unnecessary injections
 * But it has no effect under cache-type=AUTO.
 *
 * @title: TuCacheCondition
 * @author: trifolium.wang
 * @date: 2023/9/20
 * @modified :
 */
@Order(Ordered.HIGHEST_PRECEDENCE + 1123)
public class TuCacheCondition extends SpringBootCondition {

    @Override
    public ConditionOutcome getMatchOutcome(ConditionContext context, AnnotatedTypeMetadata metadata) {

        String sourceClass = "";
        if (metadata instanceof ClassMetadata) {
            sourceClass = ((ClassMetadata) metadata).getClassName();
        }
        ConditionMessage.Builder message = ConditionMessage.forCondition("TuCacheCondition", sourceClass);

        Environment environment = context.getEnvironment();

        BindResult<TuCacheProfilesConfigure.CacheType> cacheTypeBindResult
                = Binder.get(environment).bind("tucache.cache-type", TuCacheProfilesConfigure.CacheType.class);

        TuCacheProfilesConfigure.CacheType cacheType;
        if (cacheTypeBindResult.isBound()) {
            cacheType = cacheTypeBindResult.get();
        } else {
            cacheType = TuCacheProfilesConfigure.CacheType.AUTO;
        }

        ConditionOutcome outcome = null;

        ConditionMessage msg = message.because("tucache.cache-type=" + cacheType);
        switch (cacheType) {
            case REDIS:
                if (RedisCacheServiceConfigure.class.getName().equals(sourceClass)) {
                    outcome = ConditionOutcome.match(msg);
                }
                break;
            case LOCAL:
                if (LocalCacheServiceConfigure.class.getName().equals(sourceClass)) {
                    outcome = ConditionOutcome.match(msg);
                }
                break;
            case AUTO:
                outcome = ConditionOutcome.match(msg);
        }

        if (outcome == null) {
            outcome = ConditionOutcome.noMatch(message.because(" but " + msg));
        }

        return outcome;
    }
}
