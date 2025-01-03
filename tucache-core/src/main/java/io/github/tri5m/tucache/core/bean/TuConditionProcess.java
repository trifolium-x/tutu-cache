package io.github.tri5m.tucache.core.bean;

import io.github.tri5m.tucache.core.annotation.TuCache;
import io.github.tri5m.tucache.core.annotation.TuCacheClear;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.context.expression.BeanFactoryResolver;
import org.springframework.context.expression.MethodBasedEvaluationContext;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.util.StringUtils;

import java.lang.reflect.Method;

/**
 * 对tu-cache condition的支持
 *
 * @author wangxudong
 * @date 2020/08/28
 * @see TuCache , TuCacheClear
 */
public class TuConditionProcess {

    private final BeanFactory beanFactory;

    public TuConditionProcess(BeanFactory beanFactory){
        this.beanFactory = beanFactory;
    }

    public boolean accept(String conditionStr, Object rootObject, Method method, Object[] arguments) {

        if (!StringUtils.hasLength(conditionStr) || "true".equals(conditionStr)) {

            return true;
        }
        ExpressionParser parser = new SpelExpressionParser();
        StandardEvaluationContext context = new MethodBasedEvaluationContext(rootObject, method, arguments,
                new DefaultParameterNameDiscoverer());

        // 加入使用@符号访问bean能力
        context.setBeanResolver(new BeanFactoryResolver(beanFactory));

        return Boolean.TRUE.equals(parser.parseExpression(conditionStr).getValue(context, Boolean.class));
    }

}
