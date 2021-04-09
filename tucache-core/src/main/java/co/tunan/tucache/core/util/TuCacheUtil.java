package co.tunan.tucache.core.util;

import org.springframework.context.expression.MethodBasedEvaluationContext;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.ParserContext;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.util.StringUtils;

import java.lang.reflect.Method;

/**
 * @author: wangxudong
 * @date: 2021/4/9
 * @version: 1.0
 * @modified :
 */
public class TuCacheUtil {

    public static String parseKey(String spEl, Object targetObj, Method method, String keyPrefix, Object[] args) {
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

        if (keyPrefix == null) {
            keyPrefix = "";
        }

        return keyPrefix + parser.parseExpression(spEl, parserContext).getValue(context, String.class);
    }

    private static String defaultKey(Method method, Object[] args) {
        String defaultKey = method.getDeclaringClass().getPackage().getName() + method.getDeclaringClass().getName() + ":" + method.getName();
        StringBuilder builder = new StringBuilder(defaultKey);
        for (Object a : args) {
            builder.append(a.hashCode()).append("_");
        }

        return builder.toString();
    }
}
