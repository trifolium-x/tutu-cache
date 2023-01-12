package co.tunan.tucache.example.controller;

import co.tunan.tucache.core.annotation.TuCache;
import co.tunan.tucache.core.annotation.TuCacheClear;
import co.tunan.tucache.example.model.TestEnum;
import co.tunan.tucache.example.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 为了方便将缓存注解直接放到Controller，实际场景中建议放在Service或者Component中
 *
 * @title: BaseTestController
 * @author: trifolium.wang
 * @date: 2022/7/1
 * @modified :
 */
@RestController
@RequestMapping("/")
public class BaseTestController {

    private static final Logger log = LoggerFactory.getLogger(BaseTestController.class);

    @GetMapping("/simple_cache")
    @TuCache(key = "simple:#{#param}", expire = 500)
    public String cacheSimple(@RequestParam String param) {

        log.debug("进入simple缓存方法");
        return System.currentTimeMillis() + param;
    }

    @GetMapping("/clear_simple_cache")
    @TuCacheClear("simple:#{#param}")
    public String clearCacheSimple(@RequestParam String param) {

        log.debug("清理simple缓存");
        return System.currentTimeMillis() + param;
    }

    @GetMapping("/keys_cache")
    @TuCache("test_keys:#{#param1}:#{#param2}")
    public String keysCache(@RequestParam String param1, String param2) {

        log.debug("加入{}:{}cache", param1, param2);
        return System.currentTimeMillis() + param1 + "," + param2;
    }

    @GetMapping("/clear_keys_cache")
    @TuCacheClear(keys = "test_keys:#{#param1}")
    public String keysCacheClear(@RequestParam String param1) {

        log.debug("清除{}开头的keys", param1);
        return System.currentTimeMillis() + param1;
    }

    @GetMapping("/bean_test")
    @TuCache("bean_fun:#{#this.thisFun()}:#{#param}")
    public String beanTest(@RequestParam String param) {

        log.debug("调用当前对象方法获取字符串作为缓存key");
        return System.currentTimeMillis() + param;
    }

    @GetMapping("/bean_test2")
    @TuCache("bean_fun2:#{@testBean.aStr()}:#{#param}")
    public String beanTest2(@RequestParam String param) {

        log.debug("调用SprigBean方法获取字符串作为缓存key");
        return System.currentTimeMillis() + param;
    }

    @GetMapping("/bean_test3")
    @TuCache("bean_obj3:#{#user.name}:#{#user.age}")
    public String objTest3(@RequestParam User user) {

        log.debug("对象字段作为缓存key");
        return System.currentTimeMillis() + user.getName() + "," + user.getAge();
    }

    @GetMapping("/condition_test")
    @TuCache(key = "condition_test:#{#param}", condition = "#param.startsWith('a')")
    public String conditionTest(@RequestParam String param) {

        log.debug("如果param是a开头的，则缓存否则不缓存");
        return System.currentTimeMillis() + param;
    }


    @GetMapping("/clear_all")
    @TuCacheClear(keys = {"simple", "test_keys", "bean_fun", "bean_obj3", "condition_test"}, async = true)
    public String clearAll() {

        log.debug("清理所有缓存");
        return "缓存全部清理!";
    }

    @GetMapping("/array_test")
    @TuCache(key = "array:test")
    public Integer[] testArray() {

        log.debug("数组类型方法返回值序列化");
        return new Integer[]{1, 2, 3, 4};
    }

    @GetMapping("/array_test2")
    @TuCache(key = "array:test2")
    public User[] testArray2() {

        log.debug("数组类型方法返回值序列化");
        return new User[]{new User(), new User(), new User(), new User()};
    }

    @GetMapping("/primitive_test")
    @TuCache(key = "primitive:test")
    public long testPrimitive() {

        log.debug("测试基本数据类型");
        return System.currentTimeMillis();
    }

    @GetMapping("/primitive_test2")
    @TuCache(key = "primitive:test2")
    public Long testPrimitive2() {

        log.debug("测试基本数据类型2");
        return System.currentTimeMillis();
    }

    @GetMapping("/test_atomic")
    @TuCache(key = "test:atomic")
    public AtomicInteger testAtomic() {

        log.debug("测试atomic");
        return new AtomicInteger(203);
    }

    @GetMapping("/test_big_decimal")
    @TuCache(key = "test:big_decimal")
    public BigDecimal testBigDecimal() {

        log.debug("测试big_decimal");
        return new BigDecimal("2934");
    }

    @GetMapping("/test_enum")
    @TuCache(key = "test_enum:test_enum")
    public TestEnum testEnum() {

        log.debug("测试枚举");
        return TestEnum.N1;
    }


    public String thisFun() {

        return "this_function";
    }

}
