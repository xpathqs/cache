package org.xpathqs.cache.base


interface ICache<K, V> {
    fun put(k: K, v: V)
    fun get(k: K): V?

    fun contains(k: K) = get(k) != null
}