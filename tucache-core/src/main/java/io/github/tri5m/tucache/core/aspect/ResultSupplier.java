package io.github.tri5m.tucache.core.aspect;

/**
 * @title: ResultSupplier
 * @author: trifolium.wang
 * @date: 2024/2/23
 * @modified:
 */
@FunctionalInterface
public interface ResultSupplier<T> {

    T get() throws Throwable;
}
