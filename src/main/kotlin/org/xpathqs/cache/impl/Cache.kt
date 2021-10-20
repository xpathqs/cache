package org.xpathqs.cache.impl

import org.xpathqs.cache.base.ICache

open class Cache<K: Any, V>: ICache<K, V> {
    protected val map = HashMap<K,V>()

    override fun put(k: K, v: V) {
        map[k] = v
    }

    override fun get(k: K): V? {
        return map[k]
    }
}