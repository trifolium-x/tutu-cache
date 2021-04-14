package co.tunan.tucache.core.bean.impl;

import co.tunan.tucache.core.bean.TuKeyGenerate;
import co.tunan.tucache.core.config.TuCacheProfiles;
import org.springframework.context.expression.MethodBasedEvaluationContext;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.ParserContext;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.util.StringUtils;

import java.lang.reflect.Method;

/**
 * Created by wangxudong on 2021/4/15.
 *
 * @version: 1.0
 * @modified :
 */
public class DefaultTuKeyGenerate implements TuKeyGenerate {
    @Override
    public String generate(TuCacheProfiles profiles, String originKey, Object rootObject, Method method, Object[] arguments) {

        // SpEL表达式为空默认返回方法名
        if (StringUtils.isEmpty(originKey)) {
            // 生成默认的key
            return defaultKey(method, arguments);
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
        StandardEvaluationContext context = new MethodBasedEvaluationContext(rootObject, method, arguments,
                new DefaultParameterNameDiscoverer());

        String keyPrefix = "";
        if (profiles.getCachePrefix() != null) {
            keyPrefix = profiles.getCachePrefix();
        }

        return keyPrefix + parser.parseExpression(originKey, parserContext).getValue(context, String.class);
    }

    private String defaultKey(Method method, Object[] args) {
        String defaultKey = method.getDeclaringClass().getPackage().getName() + method.getDeclaringClass().getName() + ":" + method.getName();
        StringBuilder builder = new StringBuilder(defaultKey);
        for (Object a : args) {
            builder.append(a.hashCode()).append("_");
        }

        return builder.toString();
    }
}
