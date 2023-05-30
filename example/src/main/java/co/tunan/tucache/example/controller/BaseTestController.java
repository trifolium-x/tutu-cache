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

import java.util.Arrays;
import java.util.List;

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

    @GetMapping("/long_test")
    @TuCache(key = "lang:test", expire = 500)
    public Long testLong() {

        log.debug("进入testLong缓存方法");
        return 0L;
    }


    @GetMapping("/object_test")
    @TuCache(key = "object:test")
    public User testObject() {

        log.debug("测试对象缓存");
        return new User();
    }

    @GetMapping("/obj_list_test")
    @TuCache(key = "obj_list:test")
    public List<User> testObjectList() {

        log.debug("数组类型方法返回值序列化");
        return Arrays.asList(testObjectArray());
    }

    @GetMapping("/obj_array_test")
    @TuCache(key = "obj_array:test")
    public User[] testObjectArray() {

        log.debug("数组类型方法返回值序列化");
        return new User[]{new User(), new User(), new User(), new User()};
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
