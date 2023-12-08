package co.tunan.tucache.core.localcache;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.util.StringUtils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 一个基于 ConcurrentHashMap 的本地缓存，可以按照层级模糊删除
 * 在树的层级越低越近于 ConcurrentHashMap 的性能
 *
 * @title: TuTreeCache
 * @author: trifolium.wang
 * @date: 2023/12/8
 * @modified:
 */
public class TuTreeCache {

    public static final long NOT_EXPIRE = -1;
    private final CacheTable cacheTable = new CacheTable();
    private final String DELIMITER;

    public TuTreeCache() {
        this.DELIMITER = ":";
        init();
    }

    public TuTreeCache(String delimiter) {
        this.DELIMITER = delimiter;
        init();
    }

    private void init() {
        // 启动一个主动清理过期缓存的线程
//        GlobalThreadPool.submit(() -> {
//            long sleepMillisSecond = 1000L;
//            for (; ; ) {
//                try {
//                    TimeUnit.MILLISECONDS.sleep(sleepMillisSecond);
//                } catch (InterruptedException e) {
//                    log.trace(e.getMessage());
//                }
//                // TODO 递归的进行清理
//                cacheTable.keySet();
//            }
//        });
    }

    /**
     * 从缓存表中查找缓存Node
     */
    public CacheNode searchNode(String key) {
        if (!StringUtils.hasLength(key)) {
            return null;
        }
        String[] keys = key.split(DELIMITER, -1);
        CacheTable currentHierarchy = cacheTable;
        CacheNode currentNode;
        for (int i = 0; i < keys.length; i++) {
            String currentKey = keys[i];
            if (currentHierarchy == null) {

                return null;
            }
            currentNode = currentHierarchy.get(currentKey);
            if (currentNode == null) {

                return null;
            }

            // 过期的情况下,清理当前路径下的节点
            if (expired(currentNode.getExpire())) {
                if (currentNode.getChild() == null || currentNode.getChild().isEmpty()) {
                    // 过期的缓存
                    currentHierarchy.remove(currentKey);
                    return null;
                }
                currentNode.setObj(null);
                currentNode.setExpire(-1);
                if (i + 1 == keys.length) {
                    // 直接返回，少一步get
                    return null;
                }
            }

            // 如果是叶子节点
            if (i + 1 == keys.length) {

                return currentNode;
            }
            currentHierarchy = currentNode.getChild();
        }
        return null;
    }

    /**
     * 向节点中插入数据
     *
     * @param timeout 过期时长 单位毫秒
     */
    public CacheNode putNode(String key, Object obj, Long timeout) {
        if (!StringUtils.hasLength(key)) {

            throw new IllegalArgumentException("add local cache key is null.");
        } else {
            String[] keys = key.split(DELIMITER, -1);
            CacheTable currentHierarchy = cacheTable;
            CacheNode currentNode = null;
            for (int i = 0; i < keys.length; i++) {
                String currentKey = keys[i];
                currentNode = currentHierarchy.get(currentKey);
                if (currentNode == null) {
                    // 不存在则创建新的节点
                    currentNode = new CacheNode(-1, null, null);
                    currentHierarchy.put(currentKey, currentNode);
                } else {
                    // 存在的话则清除路径上的过期数据
                    // 过期的情况下,清理当前路径下的节点数据，但是其他的保留
                    if (expired(currentNode.getExpire())) {
                        currentNode.setObj(null);
                        currentNode.setExpire(-1);
                    }
                }

                if (i + 1 == keys.length) {
                    // 如果是叶子节点，则存储数据节点
                    currentNode = new CacheNode(timeout == null ? -1 : (System.currentTimeMillis() + timeout),
                            currentNode.getChild(), obj);
                    currentHierarchy.put(currentKey, currentNode);

                    return currentNode;
                } else {
                    // 否则直接拿到孩子节点并继续下一轮查询
                    currentHierarchy = currentNode.getChild();
                    if (currentHierarchy == null) {
                        CacheTable child = new CacheTable();
                        currentNode.setChild(child);
                        currentHierarchy = child;
                    }
                }

            }

            return currentNode;
        }
    }

    /**
     * 移除某个指定的缓存
     */
    public void remove(String key) {
        if (!StringUtils.hasLength(key)) {
            return;
        }
        String[] keys = key.split(DELIMITER, -1);
        CacheTable currentHierarchy = cacheTable;
        CacheNode currentNode;
        for (int i = 0; i < keys.length; i++) {
            String currentKey = keys[i];
            if (currentHierarchy == null) {
                return;
            }
            currentNode = currentHierarchy.get(currentKey);
            if (currentNode == null) {

                return;
            }
            // 如果是叶子节点
            if (i + 1 == keys.length) {
                if (currentNode.getChild() == null || currentNode.getChild().isEmpty()) {
                    currentHierarchy.remove(currentKey);
                    return;
                }
                currentNode.setObj(null);
                currentNode.setExpire(-1);
            } else {
                // 如果是中间节点且已过期，清理节点
                if (expired(currentNode.getExpire())) {
                    // 如果是中间节点
                    if (currentNode.getChild() == null || currentNode.getChild().isEmpty()) {
                        currentHierarchy.remove(currentKey);
                        return;
                    } else {
                        currentNode.setObj(null);
                        currentNode.setExpire(-1);
                    }
                }
            }
            currentHierarchy = currentNode.getChild();
        }
    }

    /**
     * 移除缓存，级联移除缓存，类似于redis中的移除keys
     */
    public void removeKeys(String keyPrefix) {
        if (!StringUtils.hasLength(keyPrefix)) {
            return;
        }
        String[] keys = keyPrefix.split(DELIMITER, -1);
        CacheTable currentHierarchy = cacheTable;
        CacheNode currentNode;
        for (int i = 0; i < keys.length; i++) {
            String currentKey = keys[i];
            if (currentHierarchy == null) {
                return;
            }
            currentNode = currentHierarchy.get(currentKey);
            if (currentNode == null) {

                return;
            }
            if (i + 1 == keys.length) {
                currentHierarchy.remove(currentKey);
                return;
            }

            // 如果是中间节点且已过期，清理节点
            if (expired(currentNode.getExpire())) {
                // 如果是中间节点
                if (currentNode.getChild() == null || currentNode.getChild().isEmpty()) {
                    currentHierarchy.remove(currentKey);
                    return;
                } else {
                    currentNode.setObj(null);
                    currentNode.setExpire(-1);
                }
            }

            currentHierarchy = currentNode.getChild();
        }
    }

    /**
     * 是否过期
     */
    private boolean expired(long expire) {

        return expire >= 0 && expire <= System.currentTimeMillis();
    }

    /**
     * 缓存数据节点
     */
    @Data
    @AllArgsConstructor
    public static class CacheNode {
        private long expire;
        private CacheTable child;
        private Object obj;
    }

    static class CacheTable {
        private final Map<String, CacheNode> table = new ConcurrentHashMap<>(36);
        // 清除和标记


        public void remove(String key) {
            table.remove(key);
        }

        public void put(String key, CacheNode value) {
            table.put(key, value);
        }

        public CacheNode get(String key) {
            return table.get(key);
        }

        public boolean isEmpty() {
            return table.isEmpty();
        }
    }
}
