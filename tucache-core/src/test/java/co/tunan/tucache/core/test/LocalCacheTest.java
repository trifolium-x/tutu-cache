package co.tunan.tucache.core.test;

import co.tunan.tucache.core.cache.impl.LocalCacheService;
import co.tunan.tucache.core.config.TuCacheProfiles;
import co.tunan.tucache.core.pool.GlobalThreadPool;
import org.junit.Ignore;
import org.junit.Test;
import org.springframework.util.Assert;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @title: LocalCacheTest
 * @author: trifolium.wang
 * @date: 2023/12/7
 * @modified:
 */
@Ignore
public class LocalCacheTest {

    @Test
    public void baseTest() {
        LocalCacheService lcs = new LocalCacheService();
        String res;

        lcs.set("aaa:bbb", "bbb", 1, TimeUnit.SECONDS);
        lcs.set("aaa:bbb:ccc", "ccc", 3, TimeUnit.SECONDS);
        lcs.set("aaa:bbb:ddd", "ddd", 5, TimeUnit.SECONDS);

        res = lcs.get("aaa:bbb", String.class);
        System.out.println(res);
        Assert.notNull(res, "丢失数据");

        // 删除
        lcs.delete("aaa:bbb");

        res = lcs.get("aaa:bbb", String.class);
        Assert.isNull(res, "未删除");

        res = lcs.get("aaa:bbb:ccc", String.class);
        System.out.println(res);
        Assert.notNull(res, "下级缓存被影响");

        // 模糊删除
        lcs.deleteKeys("aaa:bbb");
        res = lcs.get("aaa:bbb:ccc", String.class);
        Assert.isNull(res, "没有模糊删除ccc");
        res = lcs.get("aaa:bbb:ddd", String.class);
        Assert.isNull(res, "没有模糊删除ddd");
    }

    @Test
    public void testInteraction() {
        LocalCacheService lcs = new LocalCacheService();
        String res;
        lcs.set("aaa:bbb:ccc", "ccc", 3, TimeUnit.SECONDS);
        lcs.set("aaa:bbb:ddd", "ddd", 5, TimeUnit.SECONDS);
        lcs.set("aaa:bbb", "bbb", 1, TimeUnit.SECONDS);

        res = lcs.get("aaa:bbb:ccc", String.class);
        Assert.notNull(res, "丢失下级缓存");

        lcs.delete("aaa:bbb:ddd");
        res = lcs.get("aaa:bbb", String.class);
        Assert.notNull(res, "丢失上级缓存");

        lcs.deleteKeys("aaa:bbb:ccc");
        res = lcs.get("aaa:bbb", String.class);
        Assert.notNull(res, "模糊删除上级缓存被影响");
    }

    @Test
    public void testTimeout() {
        LocalCacheService lcs = new LocalCacheService();
        String res;
        lcs.set("aaa:bbb", "bbb", 1, TimeUnit.SECONDS);
        lcs.set("aaa:bbb:ccc", "ccc", 3, TimeUnit.SECONDS);
        lcs.set("aaa:bbb:ddd", "ddd", 5, TimeUnit.SECONDS);

        wait(2);
        res = lcs.get("aaa:bbb", String.class);
        Assert.isNull(res, "过期未生效");
        res = lcs.get("aaa:bbb:ccc", String.class);
        Assert.notNull(res, "上级过期影响了下级缓存");

        wait(2);
        res = lcs.get("aaa:bbb:ccc", String.class);
        Assert.isNull(res, "过期未生效");

        wait(1);
        res = lcs.get("aaa:bbb:ddd", String.class);
        Assert.isNull(res, "过期未生效");
    }

    /**
     * LocalCache 基准测试(单线程)，m1pro 8c
     * <p>
     *     最多50层的，平均 50*(2/9)+1 层  50万数据，进行[添加][查询][删除]，
     * </p>
     * <li>插入： 5082 ms</li>
     * <li>查询： 1830 ms</li>
     * <li>删除： 855 ms</li>
     *
     * <p>
     *     更加真实的情况， 最多20层的，平均 20*(2/9)+1 层  10万数据，进行[添加][查询][删除]，
     * </p>
     * <li>插入： 105 ms</li>
     * <li>查询： 338 ms</li>
     * <li>删除： 76 ms</li>
     */
    @Test
    public void benchmarkingTest(){
        LocalCacheService lcs = new LocalCacheService();
        String[] art = new String[]{"a", "b", "c", "d", "e", "f", "g", ":", ":"};
        int dataCount = 100_000;
        String[] keys = new String[100_000];

        System.out.println("准备测试数据.");

        ThreadLocalRandom random = ThreadLocalRandom.current();
        for (int i = 0; i < dataCount; i++) {
            String key = Stream.generate(() -> art[random.nextInt(9)]).limit(20).collect(Collectors.joining());
            while (key.startsWith(":")) {
                key = key.substring(1);
            }
            keys[i] = key;
        }

        System.out.println("测试插入数据");
        long startTime = System.currentTimeMillis();
        for (int i = 0; i < dataCount; i++) {
            lcs.set(keys[i], keys[i], 7, TimeUnit.SECONDS);
        }
        System.out.println("耗时："+(System.currentTimeMillis() - startTime)+" ms");

        System.out.println("测试查询数据");
        startTime = System.currentTimeMillis();
        for (int i = 0; i < dataCount; i++) {
            lcs.get(keys[i],String.class);
        }
        System.out.println("耗时："+(System.currentTimeMillis() - startTime)+" ms");

        System.out.println("测试删除数据");
        startTime = System.currentTimeMillis();
        for (int i = 0; i < dataCount; i++) {
            lcs.delete(keys[i]);
        }
        System.out.println("耗时："+(System.currentTimeMillis() - startTime)+" ms");
    }

    /**
     * LocalCache 并发测试
     * <p></p>
     * 测试平台，m1pro 8c 默认线程池配置
     * <p></p>
     * 测试50万数据的[写入]，[查询]，[随机查询]，[删除]。
     * <p></p>
     * 结果：  max_memory: 2.0G, time_consuming: 6187 ms
     */
    @Test
    public void concurrentBenchmarkTest() {
        LocalCacheService lcs = new LocalCacheService();
        GlobalThreadPool.init(new TuCacheProfiles());

        String[] art = new String[]{"a", "b", "c", "d", "e", "f", "g", ":", ":"};
        long startTime = System.currentTimeMillis();

        // 每次500个[写入，查询，删除]任务
        int taskNum = 500;

        // 每个任务1000个数据
        int multiple = 1000;

        // 数据矩阵
        String[][] keys = new String[taskNum][multiple];
        for (int i = 0; i < taskNum; i++) {
            int finalI = i;
            // 写入数据
            GlobalThreadPool.submit(() -> {
                ThreadLocalRandom random = ThreadLocalRandom.current();
                for (int j = 0; j < multiple; j++) {
                    String key = Stream.generate(() -> art[random.nextInt(9)]).limit(50).collect(Collectors.joining());
                    while (key.startsWith(":")) {
                        key = key.substring(1);
                    }
                    keys[finalI][j] = key;
                    lcs.set(key, random.nextInt(), random.nextInt(100), TimeUnit.SECONDS);
                }
            });

            // 查询存在的数据
            GlobalThreadPool.submit(() -> {
                for (int j = 0; j < multiple; j++) {
                    lcs.get(keys[finalI][j], int.class);
                }
            });

            // 随机查询包含不存在的数据
//            GlobalThreadPool.submit(() -> {
//                ThreadLocalRandom random = ThreadLocalRandom.current();
//                for (int j = 0; j < multiple; j++) {
//                    String key = Stream.generate(() -> art[random.nextInt(9)]).limit(50).collect(Collectors.joining());
//                    lcs.get(key, int.class);
//                }
//            });

            // 删除
            GlobalThreadPool.submit(() -> {
                for (int j = 0; j < multiple; j++) {
                    lcs.delete(keys[finalI][j]);
                }
            });
        }

        int i = GlobalThreadPool.taskBlockSize();
        while (i > 0) {
            System.out.println("队列积压度:" + i);
            i = GlobalThreadPool.taskBlockSize();
            try {
                TimeUnit.MILLISECONDS.sleep(300);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
        System.out.println("用时：" + (System.currentTimeMillis() - startTime) + " ms.");
    }

    private void wait(int second) {
        System.out.printf("sleep:%d s%n", second);
        try {
            TimeUnit.SECONDS.sleep(second);
        } catch (InterruptedException ignored) {

        }
    }
}
