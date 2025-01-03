package io.github.tri5m.tucache.example.controller;

import io.github.tri5m.tucache.core.annotation.TuCache;
import io.github.tri5m.tucache.core.annotation.TuCacheClear;
import io.github.tri5m.tucache.example.model.TestEnum;
import io.github.tri5m.tucache.example.model.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * 为了方便将缓存注解直接放到Controller，实际场景中建议放在Service或者Component中
 *
 * @title: BaseTestController
 * @author: trifolium.wang
 * @date: 2022/7/1
 * @modified :
 */
@Slf4j
@RestController
@RequestMapping("/")
public class BaseTestController {

    @Autowired
    private GenericInterfacesTest<User> genericInterfacesTest;

    @GetMapping("/simple_cache")
    @TuCache(key = "simple:#{#param}", timeout = 10)
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
    @TuCache(value = "test_keys:#{#param1}:#{#param2}")
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


    @GetMapping("/long_test")
    @TuCache(key = "lang:test", timeout = 5)
    public long testLong() {
        int i = new Random().nextInt(5);
        log.debug("进入testLong缓存方法, long={}", i);
        return i;
    }


    @GetMapping("/object_test")
    @TuCache(key = "object:test", timeout = 3)
    public User testObject() {

        log.debug("测试对象缓存");
        return new User("n", new Random().nextInt(5));
    }

    @GetMapping("/obj_list_test")
    @TuCache(key = "obj_list:test", timeout = 3)
    public List<User> testObjectList() {

        log.debug("数组类型方法返回值序列化1");
        return Arrays.asList(testObjectArray());
    }

    @GetMapping("/obj_array_test")
    @TuCache(key = "obj_array:test", timeout = 3)
    public User[] testObjectArray() {

        log.debug("数组类型方法返回值序列化2");
        return new User[]{new User("n1", new Random().nextInt(5)), new User("n2", new Random().nextInt(5))};
    }

    @GetMapping("/test_enum")
    @TuCache(key = "test_enum:test_enum", timeout = 60)
    public TestEnum testEnum() {

        log.debug("测试枚举");
        return TestEnum.N1;
    }

    @GetMapping("/test_generic")
    @TuCache(key = "test_generic:test_generic", timeout = 60)
    public List<User> testGeneric(){

        log.debug("测试泛型接口");
        return genericInterfacesTest.getUsers();
    }

    @GetMapping("/test_generic2")
    @TuCache(key = "test_generic2:test_generic2", timeout = 60)
    public User testGeneric2(){

        log.debug("测试泛型接口2");
        return genericInterfacesTest.getUser();
    }


    @GetMapping("/clear_caches")
    @TuCacheClear(keys = {"simple", "test_keys", "bean_fun", "bean_obj3", "condition_test", "test_enum"}, async = true)
    public String clearCaches() {

        log.debug("清理所有缓存");
        return "缓存全部清理!";
    }

    public String thisFun() {

        return "this_function";
    }
}
